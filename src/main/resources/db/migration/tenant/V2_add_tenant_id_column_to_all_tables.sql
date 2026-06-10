/* utile dans la demarche multi tenant mono base de données sans les schemas
/*-- categories
alter table categories
    add tenant_id varchar(255) not null;

comment on column categories.tenant_id is 'Tenant id';

-- products
alter table products
    add tenant_id varchar(255) not null;

comment on column products.tenant_id is 'Tenant id';

-- stock_mvts
alter table stock_mvts
    add tenant_id varchar(255) not null;

comment on column stock_mvts.tenant_id is 'Tenant id';*/
