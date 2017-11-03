-- {^} - delimiter for transaction related sql batch
-- {;} - delimiter for sql queries
create table sec_user (
	id UUID default RANDOM_UUID() not null primary key,
	login varchar(50) unique not null
)^
create table ts_time_entry (
	id UUID DEFAULT RANDOM_UUID() not null primary key,
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
INSERT INTO ts_time_entry (user_id, date_, time_in_minutes)
	select DISTINCT u.id as user_id,
									(YEAR(CURDATE()) || '-' || to_char(month.x, 'FM00')||'-'|| to_char(day.x, 'FM00'))::date as date_,
									((random() * random()) * 8 + 2)::int time_in_minutes
	from sec_user u,
				SYSTEM_RANGE(1, random(30) * 28 + 2, 2) as day,
				SYSTEM_RANGE(1, random(30) * 11 + 1) as month
;


