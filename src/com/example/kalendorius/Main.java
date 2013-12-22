package com.example.kalendorius;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener
	{
		private Button prevMonth, nextMonth;
		private TextView currentMonth;
		private static TextView currentDate;
		private GridView calendarView;
		private GridCellAdapter adapter;
		private Calendar _calendar;
		private int month, year;
		static SharedPreferences prefs;
		private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMM");
		private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy MMMM dd");


		@Override
		public void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.main);
			    prefs = this.getSharedPreferences("DUOMENYS", Context.MODE_PRIVATE);
				
				_calendar = Calendar.getInstance(Locale.getDefault());
				month = _calendar.get(Calendar.MONTH);
				year = _calendar.get(Calendar.YEAR);

				
				prevMonth = (Button) this.findViewById(R.id.prevMonth);
				prevMonth.setOnClickListener(this);

				currentMonth = (TextView) this.findViewById(R.id.currentMonth);
				String date = sdf.format(_calendar.getTime());
				currentMonth.setText(date);
				
				currentDate = (TextView) this.findViewById(R.id.currentDate);
				String date2 = fmt.format(_calendar.getTime());
				currentDate.setText("Siandienos data: "+date2);

				nextMonth = (Button) this.findViewById(R.id.nextMonth);
				nextMonth.setOnClickListener(this);

				calendarView = (GridView) this.findViewById(R.id.calendar);

				adapter = new GridCellAdapter(getApplicationContext(), R.id.gridcell, month, year);
				adapter.notifyDataSetChanged();
				calendarView.setAdapter(adapter);

			}

		@Override
		public void onClick(View v)
			{
				if (v == prevMonth)
					{
						if (month <= 1)
							{
								month = 11;
								year--;
							} else
							{
								month--;
							}

						adapter = new GridCellAdapter(getApplicationContext(), R.id.gridcell, month, year);
						_calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
						String date = sdf.format(_calendar.getTime());
						currentMonth.setText(date);
						
						adapter.notifyDataSetChanged();
						calendarView.setAdapter(adapter);
					}
				if (v == nextMonth)
					{
						if (month >= 11)
							{
								month = 0;
								year++;
							} else
							{
								month++;
							}

						adapter = new GridCellAdapter(getApplicationContext(), R.id.gridcell, month, year);
						_calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
						String date = sdf.format(_calendar.getTime());
						currentMonth.setText(date);
						adapter.notifyDataSetChanged();
						calendarView.setAdapter(adapter);
					}
			}

		//
		public class GridCellAdapter extends BaseAdapter implements OnClickListener
			{
				private final Context _context;
				private final List<String> list;
				private final String[] weekdays = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
				private final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
				private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
				private final int month, year;
				int daysInMonth, prevMonthDays;
				private final int currentDayOfMonth;
				private Button gridcell;

				// Days in Current Month
				public GridCellAdapter(Context context, int textViewResourceId, int month, int year)
					{
						super();
						this._context = context;
						this.list = new ArrayList<String>();
						this.month = month;
						this.year = year;
						Calendar calendar = Calendar.getInstance();
						currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
						
					   
						
						printMonth(month, year);
					}

				public String getItem(int position)
					{
						return list.get(position);
					}

				@Override
				public int getCount()
					{
						return list.size();
					}

				private void printMonth(int mm, int yy)
					{
						// The number of days to leave blank at
						// the start of this month.
						int trailingSpaces = 0;
						int daysInPrevMonth = 0;
						int prevMonth = 0;
						int prevYear = 0;
						int nextMonth = 0;
						int nextYear = 0;

						GregorianCalendar cal = new GregorianCalendar(yy, mm, currentDayOfMonth);

						// Days in Current Month
						daysInMonth = daysOfMonth[mm];
						int currentMonth = mm;
						if (currentMonth == 11)
							{
								prevMonth = 10;
								daysInPrevMonth = daysOfMonth[prevMonth];
								nextMonth = 0;
								prevYear = yy;
								nextYear = yy + 1;
							} else if (currentMonth == 0)
							{
								prevMonth = 11;
								prevYear = yy - 1;
								nextYear = yy;
								daysInPrevMonth = daysOfMonth[prevMonth];
								nextMonth = 1;
							} else
							{
								prevMonth = currentMonth - 1;
								nextMonth = currentMonth + 1;
								nextYear = yy;
								prevYear = yy;
								daysInPrevMonth = daysOfMonth[prevMonth];
							}

						// Compute how much to leave before before the first day of the
						// month.
						// getDay() returns 0 for Sunday.
						trailingSpaces = cal.get(Calendar.DAY_OF_WEEK) - 1;

						if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1)
							{
								++daysInMonth;
							}

						// Trailing Month days
						for (int i = 0; i < trailingSpaces; i++)
							{
								list.add(String.valueOf((daysInPrevMonth - trailingSpaces + 1) + i) + "-GREY" + "-" + months[prevMonth] + "-" + prevYear);
							}

						// Current Month Days
						for (int i = 1; i <= daysInMonth; i++)
							{
								list.add(String.valueOf(i) + "-WHITE" + "-" + months[mm] + "-" + yy);
							}

						// Leading Month days
						for (int i = 0; i < list.size() % 7; i++)
							{
								list.add(String.valueOf(i + 1) + "-GREY" + "-" + months[nextMonth] + "-" + nextYear);
							}
					}

				@Override
				public long getItemId(int position)
					{
						return position;
					}

				@Override
				public View getView(int position, View convertView, ViewGroup parent)
					{
						View row = convertView;
						if (row == null)
							{
								// ROW INFLATION
								LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								row = inflater.inflate(R.layout.gridcell, parent, false);

							}

						gridcell = (Button) row.findViewById(R.id.gridcell);
						gridcell.setOnClickListener(this);

						// ACCOUNT FOR SPACING

						String[] day_color = list.get(position).split("-");
						gridcell.setText(day_color[0]);
						gridcell.setTag(day_color[0] + "-" + day_color[2] + "-" + day_color[3]);
					
						

						if (day_color[1].equals("GREY"))
							{
								gridcell.setTextColor(Color.LTGRAY);
							}
						if (day_color[1].equals("WHITE"))
							{
								if(prefs.getInt(gridcell.getTag().toString(), 0) == 1)
									gridcell.setTextColor(Color.BLUE);
								else
									gridcell.setTextColor(Color.WHITE);
							}
						if (position == currentDayOfMonth)
							{
								gridcell.setTextColor(Color.WHITE);
							}
	
						return row;
					} 
						
				
				@Override
				public void onClick(View view)
					{
						String name = (String) view.getTag();						
						
						Intent cam = new Intent(Main.this, CamActivity.class);
						Intent photo_name = new Intent(Main.this, CamActivity.class);
						photo_name.putExtra("data", name);
						prefs.edit().putInt(name,1).commit();
						//System.out.println(prefs.getInt(name, 0));
						startActivity(cam);
						startActivity(photo_name);
						
					}
			}
	}