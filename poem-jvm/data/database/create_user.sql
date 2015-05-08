
--VARABLES TO BE RUN IN COMMAND LINE--
-- newuser, olduser, password, idcreate -- 

--ADDING A NEW USER--

-- $psql -v olduser="'<<openid or old username | leave it empty for creating new user>>'"  -v newuser="'<<new username>>'" -v password="'<<password>>'" -v idcreate=<< 0 to change from opeid to local login, 1 to create a new user>> poem < create_user.sql

-- EXAMPLES --
-- CREATE A NEW USER --
-- psql -v olduser="''" -v password="'oryx'" -v newuser="'oryx'" -v idcreate=1 poem < create_user.sql --
-- CHANGE THE EXISTING OPENID TO LOCAL LOGIN --
--  psql -v olduser="'https://profiles.google.com/110417009981450280777'", password="'john'" -v newuser="'john'" -v idcreate=0 poem < create_user.sql --
-- CHANGE FROM LOCAL LOGIN TO OPENID (LEAVE THE PASSWORD VARIABLE EMPTY) -- 
-- psql -v olduser="'john'" -v newuser="'https://profiles.google.com/110417009981450280777'" -v password="''" -v idcreate=0 poem <create_user.sql


--CREATE A NEW USER--
--CREATES A NEW USER WITH CONDITION THAT THE USERNAME DOESNOT EXISTS--
INSERT INTO identity(id,uri)
SELECT (SELECT MAX(id) FROM identity as id)+1,:newuser
WHERE NOT EXISTS (SELECT uri FROM identity WHERE uri=:newuser OR uri=:olduser);

--SETS PASSWORD AND DATE STAMPS FOR THE USERNAME --
INSERT INTO subject(ident_id,first_login,last_login,password)
SELECT (SELECT MAX(id) FROM identity as id),CURRENT_DATE,CURRENT_DATE,:password
WHERE EXISTS (SELECT uri FROM identity WHERE uri=:newuser AND :idcreate=1);



--CHANGE FROM OPENID TO USERNAME OR VICE VERSA -- 
--RENAMES THE OLD OPENID TO THE NEW USERNAME--
UPDATE identity 
SET uri=:newuser 
WHERE uri=:olduser AND :idcreate = 0;

--UPDATES THE PASSWORD FOR THE USERNAME--
UPDATE subject
SET password=:password
WHERE ident_id=(SELECT id FROM identity WHERE uri=:newuser) AND :idcreate=0;


