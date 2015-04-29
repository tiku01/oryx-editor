DROP FUNCTION get_identity_from_hierarchy(hierarchy text);
DROP FUNCTION get_identity_id_from_hierarchy(hierarchy text);



CREATE FUNCTION get_identity_from_hierarchy(hierarchy_result text) RETURNS identity
    AS $$ DECLARE
	result identity;
BEGIN
	SELECT identity.* INTO result FROM identity, structure WHERE identity.id=structure.ident_id AND structure.hierarchy=hierarchy_result;
	RETURN result;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.get_identity_from_hierarchy(hierarchy text) OWNER TO poem;

--
-- Name: get_identity_id_from_hierarchy(text); Type: FUNCTION; Schema: public; Owner: poem
--

CREATE FUNCTION get_identity_id_from_hierarchy(hierarchy_result text) RETURNS integer
    AS $$ DECLARE
	result integer;
BEGIN
	SELECT identity.id INTO result FROM identity, structure WHERE identity.id=structure.ident_id AND structure.hierarchy=hierarchy_result;
	RETURN result;
END;$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.get_identity_id_from_hierarchy(hierarchy text) OWNER TO poem;



