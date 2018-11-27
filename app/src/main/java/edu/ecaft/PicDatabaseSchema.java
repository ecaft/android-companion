package edu.ecaft;

import android.provider.BaseColumns;

/**
 * Created by pdarb on 1/31/2018.
 */

public class PicDatabaseSchema {
    private PicDatabaseSchema() {
    }

    public static final class CompanyTable implements BaseColumns{
        public static final String NAME = "Pics";
        public static final String COMPANY_NAME = "Company_names";
        public static final String PICFILES = "Picture_files";

    }
}
