CREATE TABLE "group_salary_columns"
(
    "id"          bigserial PRIMARY KEY,
    "code"        varchar(50)  NOT NULL,
    "name"        varchar(255) NOT NULL,
    "description" text,
    "group_column_type" varchar(50),
    "created_at"   timestamp,
    "updated_at" timestamp
);

CREATE TABLE "salary_columns"
(
    "id"                      bigserial PRIMARY KEY,
    "group_salary_columns_id" bigint,
    "code"                    varchar(50)  NOT NULL,
    "name"                    varchar(255) NOT NULL,
    "column_type"             varchar(50),
    "income_type"             varchar(50)
);

CREATE TABLE "salary_templates"
(
    "id"              bigserial PRIMARY KEY,
    "code"            varchar(50)  NOT NULL,
    "name"            varchar(255) NOT NULL,
    "description"     text,
    "template_type"   varchar(50)  NOT NULL,
    "loop_type"       varchar(50)  NOT NULL,
    "start_date"      timestamp,
    "applicable_type" varchar(50)
);

CREATE TABLE "salary_template_applicables"
(
    "id"                  bigserial PRIMARY KEY,
    "salary_templates_id" bigint,
    "department_id"       bigint,
    "staff_id"            bigint
);

CREATE TABLE "salary_templates_salary_columns"
(
    "id"                      bigserial PRIMARY KEY,
    "salary_templates_id"     bigint,
    "group_salary_columns_id" bigint,
    "salary_columns_id"       bigint
);

ALTER TABLE "salary_columns"
    ADD FOREIGN KEY ("group_salary_columns_id") REFERENCES "group_salary_columns" ("id");

ALTER TABLE "salary_template_applicables"
    ADD FOREIGN KEY ("salary_templates_id") REFERENCES "salary_templates" ("id");

ALTER TABLE "salary_templates_salary_columns"
    ADD FOREIGN KEY ("salary_templates_id") REFERENCES "salary_templates" ("id");

ALTER TABLE "salary_templates_salary_columns"
    ADD FOREIGN KEY ("group_salary_columns_id") REFERENCES "group_salary_columns" ("id");

ALTER TABLE "salary_templates_salary_columns"
    ADD FOREIGN KEY ("salary_columns_id") REFERENCES "salary_columns" ("id");
