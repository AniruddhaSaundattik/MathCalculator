CREATE TABLE MYCALC
(MYSEQ BIGINT NOT NULL, PRIMARY KEY (MYSEQ),
MYFORMULA TEXT, MYRESULT TEXT, CREATEDATE TIMESTAMP DEFAULT NOW());