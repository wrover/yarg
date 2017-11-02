create table sec_user (
	id UUID DEFAULT UUID() not null primary key,
	login varchar(50) not null
)^
create unique index idx_sec_user_uniq_login on sec_user (login);
create table ts_time_entry (
	id UUID DEFAULT UUID() not null primary key,
	user_id UUID not null references sec_user(id),
	date_ date not null,
	time_in_minutes integer default 0 not null
)^
create TABLE months (
	id int primary key,
	name varchar(50)
);
INSERT INTO months (name, id) VALUES
	('January', 1),
	('February', 2),
	('March', 3),
	('April', 4),
	('May', 5),
	('June', 6),
	('July', 7),
	('August', 8),
	('September', 9),
	('October', 10),
	('November', 11),
	('December', 12)
;

INSERT INTO sec_user (login) VALUES
	('Tedd'),
	('Fred'),
	('Dead')
;
