package com.mallardsoft.tuple;

public class Octuple<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple<T1, Tuple<T2, Tuple<T3, Tuple<T4, Tuple<T5, Tuple<T6, Tuple<T7, Tuple<T8, End>>>>>>>> {

	public Octuple(T1 m1, T2 m2, T3 m3, T4 m4, T5 m5, T6 m6, T7 m7, T8 m8) {
		super(m1, Tuple.from(m2, m3, m4, m5, m6, m7, m8));
	}

}
