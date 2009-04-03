package org.configureme.environments;

import java.util.Locale;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

public class LocaleBasedEnvironment implements Environment{
	
	private Locale locale;
	
	public LocaleBasedEnvironment(Locale aLocale){
		//ensure class is immutable
		locale = (Locale) aLocale.clone();
	}
	
	private LocaleBasedEnvironment(Builder builder){
		locale = new Locale(builder.language, builder.country, builder.variant);
	}
	
	public static class Builder{
		private String language;
		private String country;
		private String variant;
		
		public Builder(){
			language = "";
			country = "";
			variant = "";
		}

		public Builder(Locale aLocale){
			language = aLocale.getLanguage();
			country = aLocale.getCountry();
			variant = aLocale.getVariant();
		}
		
		public LocaleBasedEnvironment build(){
			return new LocaleBasedEnvironment(this);
		}
		
		public Builder country(String value){
			country = value; return this;
		}
		public Builder language(String value){
			language = value; return this;
		}
		public Builder variant(String value){
			variant = value; return this;
		}
		
	}
	
	public String toString(){
		return locale.toString();
	}

	public String expandedStringForm(){
		return toString();
	}

	@Override
	public boolean isReduceable() {
		return !isEmpty(locale.getLanguage());
	}

	@Override
	public Environment reduce() {
		if (!isEmpty(locale.getVariant())){
			return new LocaleBasedEnvironment(new Locale(locale.getLanguage(), locale.getCountry(), reduceVariant(locale.getVariant())));
		}
		
		if (!isEmpty(locale.getCountry()))
			return new LocaleBasedEnvironment(new Locale(locale.getLanguage()));
		
		if (!isEmpty(locale.getLanguage()))
			return GlobalEnvironment.INSTANCE;
		
		throw new AssertionError("Can't happen, have you called isReduceable() previous to reduce()?");
		
	}
	
	private static String reduceVariant(String variant){
		if (isEmpty(variant))
			throw new AssertionError("Shouldn't happen, can't reduce non existent variant");
		
		int indexOfUnderscore = variant.lastIndexOf('_');
		if (indexOfUnderscore==-1)
			return "";
		return variant.substring(0, indexOfUnderscore);
	}
	
	private static boolean isEmpty(String s){
		return s==null || s.length()==0;
	}
	
	public static void main(String a[]){
		System.out.println("START");
		LocaleBasedEnvironment bayern = new LocaleBasedEnvironment.Builder(Locale.GERMANY).variant("bayern_munich").build();
		Environment e = bayern;
		System.out.println(e);
		do{
			e = e.reduce();
			System.out.println(e);
			
		}while(e.isReduceable());
		System.out.println("DONE");
			
	}
}
