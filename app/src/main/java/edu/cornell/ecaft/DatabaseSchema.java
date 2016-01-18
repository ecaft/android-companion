package edu.cornell.ecaft;

/**
 * Created by Ashley on 1/16/2016.
 */
public class DatabaseSchema {
    public static final class CompanyTable {
        public static final String NAME = "companies";


        public static final class Cols {
            public static final String KEY_ID = "_id";
            public static final String UUID = "uuid";
            public static final String COMPANY_NAME = "name";
            public static final String VISITED = "visited";
        }
    }
}
