package com.mallardsoft.tuple;

public class Quintuple<T1, T2, T3, T4, T5> extends Tuple<T1, Tuple<T2, Tuple<T3, Tuple<T4, Tuple<T5, End>>>>> {

	public Quintuple(T1 m1, T2 m2, T3 m3, T4 m4, T5 m5) {
		super(m1, Tuple.from(m2, m3, m4, m5));
	}

}
