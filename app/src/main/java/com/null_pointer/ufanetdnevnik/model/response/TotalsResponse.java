package com.null_pointer.diarycloud.model.response;

public class TotalsResponse extends CommonResponse{
    private Page page;

    public Page getPage(){
        return page;
    }

    public class Page{
        private String table;
        private boolean success;

        public boolean isSuccess(){
            return success;
        }
        public String getHtmlTable(){
            return table;
        }
    }
}
