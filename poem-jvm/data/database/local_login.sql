

--
-- Data for Name: plugin; Type: TABLE DATA; Schema: public; Owner: poem
-- Adding local login plugin

COPY plugin (rel, title, description, java_class, is_export) FROM stdin;
/local_login	LocalLogin	Enable Local Login	org.b3mn.poem.handler.LocalLogin	f
