package com.memoseed.simpletwitterclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;

public class TWParameters {

	SharedPreferences TwitterSettings;
	Editor editor;
	public static final String APP_PREFERENCES = "TwitterPrefs";
	Context context;
	private Map<String, ?> allSettings;


	public TWParameters(Context context)
	{
		this.context = context;		
	}

	private void openConnection() 
	{
		TwitterSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		editor = TwitterSettings.edit();
	}

	private void closeConnection() 
	{
		editor = null;
		TwitterSettings = null;
	}
	
	public void setInt(int value,String key)
	{
		openConnection();		
		editor.putInt(key, value);		
		editor.commit();
		closeConnection();
	}
	public void setLoong(long value,String key)
	{
		openConnection();
		editor.putLong(key, value);
		editor.commit();
		closeConnection();
	}
	public void setFloat(float value,String key)
	{
		openConnection();		
		editor.putFloat(key, value);		
		editor.commit();
		closeConnection();
	}
	public void setString(String value, String key)
	{
		openConnection();		
		editor.putString(key, value);		
		editor.commit();
		closeConnection();
	}
	
	
	public void setBoolean(Boolean value, String key)
	{
		openConnection();
		editor.putBoolean(key, value);
		editor.commit();
		closeConnection();
	}
	
	
	public boolean getBoolean(String key, boolean defValue)
	{
		boolean result = defValue;
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getBoolean(key, defValue);
		}

		closeConnection();
		return result;
	}
	
	public int getInt(String key)
	{
		int result = 0;
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getInt(key, 0);
			closeConnection();
		}

		return result;
	}
	
	public int getInt(String key, int defValue)
	{
		int result = defValue;
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getInt(key, defValue);
			closeConnection();
		}

		return result;
	}
	
	public float getFloat(String key)
	{
		float result = 0;
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getFloat(key, 0);
			closeConnection();
		}

		return result;
	}
	public String getString(String key)
	{
		String result = "";
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getString(key, "");
			closeConnection();
		}

		return result;
	}

	public String getString(String key, String strdefault)
	{
		String result = strdefault;
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getString(key, strdefault);
			closeConnection();
		}

		return result;
	}
	public long getLoong(String key, long defValue)
	{
		long result = defValue;
		openConnection();

		if (TwitterSettings!=null && TwitterSettings.contains(key)) {
			result = TwitterSettings.getLong(key, defValue);
			closeConnection();
		}

		return result;
	}



	public String getAllSettings() {
		openConnection();

		Map<String,?> keys = TwitterSettings.getAll();

//		for(Map.Entry<String,?> entry : keys.entrySet()){
//			Log.d("map values",entry.getKey() + ": " +
//					entry.getValue().toString());
//		}

		String allSettings=keys.toString();
		closeConnection();
		return allSettings;
	}

	public void clearAllSettings() {
		openConnection();
		editor.clear();
		editor.commit();
		closeConnection();
	}

	public void clearAllSettings(String parameterToKeepKey) {
		String parameterToKeep = getString(parameterToKeepKey);
		openConnection();
		editor.clear();
		editor.commit();
		closeConnection();
		setString(parameterToKeep,parameterToKeepKey);
	}
}
