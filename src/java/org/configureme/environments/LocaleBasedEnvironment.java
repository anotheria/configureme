package org.configureme.environments;

import java.util.Locale;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

/** 
 * This is an environment imlementation for locale based environments, i.e. environments which are depending on languages. They are still fully cascading but backed by java.util.Locale.
 * An example environment would be de_DE_bavaria_munich, en_US_california_sandiego etc.
 */
public class LocaleBasedEnvironment implements Environment{
	
	/**
	 * The internal locale
	 */
	private Locale locale;
	
	/**
	 * Creates a new environment from a given Locale.
	 * @param aLocale a java.util.Locale.
	 */
	public LocaleBasedEnvironment(Locale aLocale){
		//ensure class is immutable
		locale = new Locale(aLocale.getLanguage().trim(), aLocale.getCountry().trim(), aLocale.getVariant().trim());
	}
	
	/**
	 * Creates a new LocaleBasedEnvironment from the Builder.
	 * @param builder
	 */
	private LocaleBasedEnvironment(Builder builder){
		locale = new Locale(builder.language, builder.country, builder.variant);
	}
	
	/**
	 * The Builder for the LocaleBasedEnvironment. A convenient way of building LocaleBasedEnvironment. Pattern from Bloch's Effective Java Sec. Edition.  
	 * @author another
	 */
	public static class Builder{
		/**
		 * The language part of the locale
		 */
		private String language;
		/**
		 * The country part of the locale
		 */
		private String country;
		/**
		 * The variant. The variant itself can be further cascading by using '_' to separate pars.
		 */
		private String variant;
		
		/**
		 * Creates a new builder.
		 */
		public Builder(){
			language = "";
			country = "";
			variant = "";
		}

		/**
		 * Creates a new builder, preinitialized by the given locale.
		 * @param aLocale
		 */
		public Builder(Locale aLocale){
			language = aLocale.getLanguage();
			country = aLocale.getCountry();
			variant = aLocale.getVariant();
		}
		
		/**
		 * Creates a new LocaleBasedEnvironment from this build.
		 * @return
		 */
		public LocaleBasedEnvironment build(){
			return new LocaleBasedEnvironment(this);
		}
		
		/**
		 * Sets the country. Returns self for chaining.
		 */
		public Builder country(String value){
			country = value.trim(); return this;
		}
		/**
		 * Sets the language. Returns self for chaining.
		 */
		public Builder language(String value){
			language = value.trim(); return this;
		}
		
		/**
		 * Sets the variant. Returns self for chaining.
		 */
		public Builder variant(String value){
			variant = value.trim(); return this;
		}
		
	}
	
	@Override
	public String toString(){
		return locale.toString();
	}

	@Override
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
	
	/**
	 * This function is used internally to reduce the variant -> a_b_c -> a_b
	 * @param variant the variant to reduce
	 * @return
	 */
	private static String reduceVariant(String variant){
		if (isEmpty(variant))
			throw new AssertionError("Shouldn't happen, can't reduce non existent variant");
		
		int indexOfUnderscore = variant.lastIndexOf('_');
		if (indexOfUnderscore==-1)
			return "";
		return variant.substring(0, indexOfUnderscore);
	}
	
	/**
	 * Returns true if the string is null or empty.
	 * @param s string to check
	 * @return
	 */
	private static boolean isEmpty(String s){
		return s==null || s.length()==0;
	}
	
	/**
	 * Two LocaleBasedEnvironments are equal to each other if the underlying locales are equal.
	 */
	@Override
	public boolean equals(Object o){
		return o instanceof LocaleBasedEnvironment && ((LocaleBasedEnvironment)o).locale.equals(locale);
	}
	
}
