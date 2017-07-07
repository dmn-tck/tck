/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
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

package org.omg.dmn.tck;

import java.time.LocalDate;
import java.util.Map;

public class Vendor {
    private String                  name;
    private String                  vendorUrl;
    private String                  product;
    private String                  productUrl;
    private String                  version;
    private String                  comment;
    private Map<String, TestResult> results;
    private LocalDate               lastUpdate;

    public Vendor( String name,
                   String vendorUrl,
                   String product,
                   String productUrl,
                   String version,
                   String comment,
                   Map<String, TestResult> results,
                   LocalDate lastUpdate ) {
        this.name = name;
        this.vendorUrl = vendorUrl;
        this.product = product;
        this.productUrl = productUrl;
        this.version = version;
        this.comment = comment;
        this.results = results;
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVendorUrl() {
        return vendorUrl;
    }

    public void setVendorUrl(String vendorUrl) {
        this.vendorUrl = vendorUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, TestResult> getResults() {
        return results;
    }

    public void setResults(Map<String, TestResult> results) {
        this.results = results;
    }

    public String getFileNameId() {
        return product.replaceAll( "//s+,","" ) + "_" + version.replaceAll( "//s+", "" );
    }

    public long getSucceeded() {
        return this.results.values().stream().filter( t -> t.getResult() == TestResult.Result.SUCCESS ).count();
    }

    public long getFailed() {
        return this.results.values().stream().filter( t -> t.getResult() == TestResult.Result.ERROR ).count();
    }

    public long getIgnored() {
        return this.results.values().stream().filter( t -> t.getResult() == TestResult.Result.IGNORED ).count();
    }

    public long getSubmitted() {
        return this.results.size();
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof Vendor) ) return false;

        Vendor vendor = (Vendor) o;

        if ( name != null ? !name.equals( vendor.name ) : vendor.name != null ) return false;
        return version != null ? version.equals( vendor.version ) : vendor.version == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Vendor{" +
               "name='" + name + '\'' +
               ", vendorUrl='" + vendorUrl + '\'' +
               ", product='" + product + '\'' +
               ", productUrl='" + productUrl + '\'' +
               ", version='" + version + '\'' +
               ", comment='" + comment + '\'' +
               ", results=" + results +
               ", lastUpdate=" + lastUpdate +
               '}';
    }
}
