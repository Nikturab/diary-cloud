package com.null_pointer.diarycloud.model.response;

import java.io.Serializable;

/**
 * Created by null_pointer on 04.05.17.
 */

public class MessagesResponse extends CommonResponse{
    private Page page;

    public Page getPage() {
        return page;
    }

    public class Page{
        private Pagination pagination;
        private Contact[]  contacts;
        private Message[]  inbox;

        public Pagination getPagination() {
            return pagination;
        }

        public Contact[] getContacts() {
            return contacts;
        }

        public Message[] getInbox() {
            return inbox;
        }

        public class Message{
            private String send_date, sender, subject, content;
            int id;
            private boolean seen;

            public String getSendDate() {
                return send_date;
            }

            public String getSender() {
                return sender;
            }

            public String getSubject() {
                return subject;
            }

            public String getContent() {
                return content;
            }

            public int getId() {
                return id;
            }

            public boolean isSeen() {
                return seen;
            }
        }

        public class Pagination{
            private boolean has_next;
            private String last_page;

            public boolean hasNext() {
                return has_next;
            }

            public String getLastPage() {
                return last_page;
            }
        }

    }
}
