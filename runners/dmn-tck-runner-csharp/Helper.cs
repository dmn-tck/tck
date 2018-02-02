/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Net;
using System.Web;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using Newtonsoft.Json.Linq;

namespace DMN.TCK.Runner
{
  public class Helper
  {
    private string _url;
    private string _bearer; 
    private WebClient _webClient;

    public Helper()
    {
    }

    public bool Connect(string url, string user, string password)
    {
      _url = url;
      _webClient = new WebClient();
      _webClient.BaseAddress = url;

      var param = new System.Collections.Specialized.NameValueCollection();
      param.Add("param2", "escaping is already handled");
      param.Add("username", user);
      param.Add("password", password);

      try
      {
        _webClient.Headers[HttpRequestHeader.ContentType] = "application/x-www-form-urlencoded";
        byte[] bytes = _webClient.UploadValues(url + "/Token", param);
        string response = Encoding.UTF8.GetString(bytes);

        JObject jObject = (JObject)JsonConvert.DeserializeObject(response);
        _bearer = jObject.GetValue("AccessToken").ToString();
        if(string.IsNullOrEmpty(_bearer)) return false;

        string auth = string.Format("Bearer {0}", _bearer);
        _webClient.Headers.Add(HttpRequestHeader.Authorization, auth);
        return true;
      }
      catch { return false; }
    }

    public bool PushTestCase(string groupId, string artifactId, FileInfo dmnFile)
    {
      if (string.IsNullOrEmpty(_bearer))
      {
        return false;
      }

      string publishURL = _url;
      if (!publishURL.EndsWith("/"))
      {
        publishURL += "/";
      }
      publishURL += "execution/deployment/" + groupId + "/" + artifactId;

      try
      {
        string data = File.ReadAllText(dmnFile.FullName);
        string response = _webClient.UploadString(publishURL, data); 
        return true;
      }
      catch 
      {
        return false;
      }
    }

    public Dictionary<String, Boolean> RunTestCase(string groupId, string artifactId, FileInfo tckTestFile)
    {
      if (string.IsNullOrEmpty(_bearer))
      {
        return null;
      }

      Dictionary<String, Boolean> results = new Dictionary<string, bool>();

      string publishURL = _url;
      if (!publishURL.EndsWith("/"))
      {
        publishURL += "/";
      }
      publishURL += artifactId;

      try
      {
        string data = File.ReadAllText(tckTestFile.FullName);
        string response = _webClient.UploadString(publishURL, data);
        XmlDocument doc = new XmlDocument();
        doc.LoadXml(response);
        XmlNodeList nodes = doc.GetElementsByTagName("testCase");
        foreach (XmlNode node in nodes)
        {
          XmlElement elm = node as XmlElement;
          string passed = node.Attributes["passed"].Value;
          bool status = (passed == "True") ? true : false;
          results.Add(node.Attributes["id"].Value, status);
        }
      }
      catch
      {
        return null;
      }
      return results;
    }
  }
}