/**
 * Validates the zip code with the post.ch website
 * @param zip zip
 * @return state if the zip is valid
 */
@CheckReturnValue
public static final boolean checkZIP(int zip) {
	String line;
	BufferedReader rd = null;
	try {
		HttpURLConnection conn = (HttpURLConnection) (new URL("http://www.post.ch/db/owa/pv_plz_pack/pr_check_data?p_language=de&p_nap="+zip)).openConnection();
		conn.setRequestMethod("GET");
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) if(line.contains("Keine PLZ gefunden")) return false;
	} catch (IOException e) { System.err.println(e.getMessage()); } 
	finally { if (rd != null) try { rd.close(); } catch (IOException e) {} }
	return true;
}
