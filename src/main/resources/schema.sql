
create table if not exists bill_categories
(
	bill_id varchar(80) null,
	categories varchar(45) null
);

create table if not exists file
(
	file_name varchar(100) not null,
	id varchar(45) not null
		primary key,
	url varchar(800) null,
	upload_date varchar(100) null,
	size mediumtext null,
	md5_hash varchar(500) null,
	origin_name varchar(50) null,
	content_type varchar(50) null,
	owner_id varchar(50) null
);

create table if not exists billing
(
	id varchar(80) not null
        primary key ,
	created_ts varchar(45) null,
	updated_ts varchar(45) null,
	owner_id varchar(45) not null,
	vendor varchar(45) null,
	bill_date varchar(45) null,
	due_date varchar(45) null,
	amount_due double null,
	categories varchar(45) null,
	payment_status enum('paid', 'due', 'past_due', 'no_payment_required') null,
	attachment varchar(100) null,
	constraint id_UNIQUE
		unique (id),
	constraint file___fk
		foreign key (attachment) references file (id)
			on update cascade
);


create table if not exists user
(
	id varchar(36) charset utf8mb4 default '0' not null comment 'PRIMARY KEY'
		primary key,
	first_name varchar(45) null,
	last_name varchar(45) null,
	password varchar(80) not null,
	email varchar(45) not null,
	account_created varchar(45) null,
	account_updated varchar(45) null,
	enabled tinyint(1) null
)
charset=latin1;

create table if not exists users
(
	username varchar(50) not null
		primary key,
	password varchar(68) not null,
	enabled tinyint default 1 not null
);

create table if not exists authorities
(
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint ix_auth_username
		unique (username, authority),
	constraint authorities_ibfk_1
		foreign key (username) references users (username)
);

