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
using System.Text;

namespace DMN.TCK.Runner
{
  class Program
  {
    static void Main(string[] args)
    {
      if (args.Length < 4)
      {
        ShowUsageInfo();
        return;
      }

      try
      {
        string tckTestsRoot = args[0];
        if (!Directory.Exists(tckTestsRoot))
        {
          Console.WriteLine("Invalid DMN TCK test case folder: " + tckTestsRoot);
          ShowUsageInfo(); return;
        }

        //  Detect all test categories
        DirectoryInfo di = new DirectoryInfo(tckTestsRoot);
        DirectoryInfo[] testCategoriesFolders = di.GetDirectories();
        if (testCategoriesFolders == null || testCategoriesFolders.Length == 0)
        {
          Console.WriteLine("Invalid DMN TCK test case folder: " +
            tckTestsRoot + " it does not contain compliance sub folders");
          ShowUsageInfo(); return;
        }

        Helper helper = new Helper();
        bool connected = true; // helper.Connect(args[1], args[2], args[3]);
        if (!connected)
        {
          Console.WriteLine("Cannot connect to test server!");
          ShowUsageInfo(); return;
        }
        
        //  Inside each category, there are a list of tests
        foreach (DirectoryInfo testCategoriesFolder in testCategoriesFolders)
        {
          string category = testCategoriesFolder.Name;
          DirectoryInfo[] testFolders = testCategoriesFolder.GetDirectories();
          if (testFolders == null)
          {
            // TODO: Warning!!! continue If
            continue;
          }

          Console.WriteLine(category);
          foreach (DirectoryInfo testFolder in testFolders)
          {
            string testId = testFolder.Name;
            FileInfo[] testCasesFiles = testFolder.GetFiles("*.xml");
            foreach (FileInfo testCaseFile in testCasesFiles)
            {
              try
              {
                Console.WriteLine(testCaseFile.Name);
                try
                {
                  if (helper.PushTestCase(category, testId, testCaseFile))
                  {
                    Dictionary<string, bool> results = helper.RunTestCase(category, testId, testCaseFile);
                    int total = 0;
                    int passed = 0;
                    foreach (KeyValuePair<string, bool> result in results)
                    {
                      total++;
                      if (result.Value)
                      {
                        passed++;
                      }
                    }
                    if (total == 0)
                    {
                      Console.WriteLine("[" + "NO TESTS FOUND" + "]");
                    }
                    else if ((total == passed))
                    {
                      Console.WriteLine("[" + "PASSED " + (passed + ("/" + total)) + "]");
                    }
                    else
                    {
                      Console.WriteLine("[" + "FAILED " + (passed + ("/" + total)) + "]");
                    }
                  }
                }
                catch (Exception ex)
                {
                  Console.Write("Internal error: \n" + ex.StackTrace);
                }
              }
              catch (Exception ex)
              {
                Console.Write("Internal error: \n" + ex.StackTrace);
              }
            }
          }
        }
      }
      catch (Exception ex)
      {
        Console.Write("Internal error: \n" + ex.StackTrace);
      }
    }

    private static void ShowUsageInfo()
    {
      Console.Write("Usage: DMN.TCK.Runner [DMN TCK test case folder] [Process Server URL] [User] [Password]");
      Console.Write("---------------------------------------------------------------------------------------");
      Console.Write("If the DMN TCK folder is not provided, the current working directory will be used");
      Console.Write("");
    }
  }
}