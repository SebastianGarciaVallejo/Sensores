package com.example.sensores;

import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener,
		OnClickListener {

	private TextView x, y, z, lista;
	private Sensor sensorAcelerometro = null;
	private Sensor sensorProximidad = null;
	private SensorManager sensorManager = null;
	private Button activar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		x = (TextView) findViewById(R.id.xID);
		y = (TextView) findViewById(R.id.yID);
		z = (TextView) findViewById(R.id.zID);
		lista = (TextView) findViewById(R.id.listaID);
		activar = (Button) findViewById(R.id.btnActivar);
		activar.setOnClickListener(this);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> listaSensores = sensorManager
				.getSensorList(Sensor.TYPE_ALL); // Identifica todos los sensores del dispositivo 
		for (Sensor sensor : listaSensores) {
			concatenar(sensor.getName());
		}
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private void concatenar(String cadena) {
		lista.append(cadena + "\n"); // metodo que concatena todos los nombres de los sensores y los agrega al textview
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnActivar) { // activa el sensor de proximidad cuando se selecciona el boton Activar
			if(activar.getText() == "Activar Sensor"){
				sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
				sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
				sensorManager.registerListener(this, sensorProximidad,SensorManager.SENSOR_DELAY_NORMAL); //Indica cada cuanto tiempo el sensor debe realizar mediciones.
				activar.setText("Desactivar");
			}
			else{
				activar.setText("Activar Sensor");
				sensorManager.unregisterListener(this,sensorProximidad); //Desactiva el sensor de proximidad
			}
		}
	}

	protected void onResume() {
		super.onResume();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensores.size() > 0) {
			sensorManager.registerListener(this, sensores.get(0),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	//Si la actividad es interrumpida se apagan los sensores para ahorra bateria
	protected void onPause() { 
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.unregisterListener(this, sensorAcelerometro);
		sensorManager.unregisterListener(this,sensorProximidad);
		super.onPause();
	}

	//Si la actividad es cerrada es necesario apagar los sensores para no descargar el telefono
	protected void onStop() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.unregisterListener(this, sensorAcelerometro);
		sensorManager.unregisterListener(this,sensorProximidad);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	
	//En este metodo detectamos por medio de un switch cual sensor esta enviando datos.
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			//se obtienen los valores medidos por el acelerometro en el espacio y se muestran en pantalla.
			case Sensor.TYPE_ACCELEROMETER:
				x.setText("X: " + event.values[SensorManager.DATA_X]);
				y.setText("Y: " + event.values[SensorManager.DATA_Y]);
				z.setText("Z: " + event.values[SensorManager.DATA_Z]);
				break;

			//Dependiendo del dato que envie el sensor de proximidad, el color de la actividad cambia entre blanco y azul  
			case Sensor.TYPE_PROXIMITY:
				float valor = event.values[0];
				if (valor <= 5) {
					int color = Color.BLUE;
					setActivityBackgroundColor(color);
				} else {
					int color = Color.WHITE;
					setActivityBackgroundColor(color);
				}
				break;
			}
		}
	}

	//Metodo para cambiar el color de fondo de la actividad.
	public void setActivityBackgroundColor(int color) {
		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
