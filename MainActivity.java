package com.example.weather;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView textView;
	ListView listView;
	List<City> data = new ArrayList<City>();
	ArrayList<String> citylist = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String address = "http://flash.weather.com.cn/wmaps/xml/china.xml";
		textView = (TextView) findViewById(R.id.text);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(
						MainActivity.this,
						"城市：" + data.get(arg2).cityname + ",天气:"
								+ data.get(arg2).stateDetailed + ",最高温度:"
								+ data.get(arg2).tem1 + ",最低温度:"
								+ data.get(arg2).tem2, Toast.LENGTH_LONG)
						.show();

			}
		});
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				Message message = new Message();
				message.what = 0;
				message.obj = response.toString();
				handler.sendMessage(message);
			}

			@Override
			public void onError(Exception e) {
				textView.setText(e.getMessage());
			}
		});

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
				String response = (String) msg.obj;
				Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
				pull(response);
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,citylist);
				listView.setAdapter(adapter);
				//textView.setText(response);
				}
		
	};

	public void pull(String xmlData) {
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlData));
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				City city = new City();
				String name = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("city".equals(name)) {
						city.cityname = parser.getAttributeValue(null,
								"cityname");
						city.stateDetailed = parser.getAttributeValue(null,
								"stateDetailed");
						city.tem1 = parser.getAttributeValue(null, "tem1");
						city.tem2 = parser.getAttributeValue(null, "tem2");
						citylist.add(city.cityname);
						data.add(city);
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
