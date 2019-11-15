package com.tools.plugin.utils;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;

import com.tools.plugin.utils.system.IpUtils;


/**
 *
 *
 */
public class StringUtils {
	public static String normalizeUrl(String url) {
		if(url == null || url.length() == 0)
			return url;
		
		//not matched, use default policy: ignore querystr
		String normalizedUrl;
		int k;
		if((k = url.indexOf('?')) != -1) {
			normalizedUrl = substring(url, 0, k);
			if((k = normalizedUrl.indexOf(';')) != -1) {
				normalizedUrl = substring(normalizedUrl, 0, k);
			}
		} else if((k = url.indexOf('&')) != -1) {
			//some URLs' params string do not proceed with '?'
			int l = url.lastIndexOf('/');
			if(k > l && l > 0 && url.indexOf('=', k) != -1) {
				boolean withoutPath = false;
				for(int ci = l + 1; ci < k; ++ci) {
					if(url.charAt(ci) == '=') {
						withoutPath = true;
						break;
					}
				}
				normalizedUrl = (withoutPath ? substring(url, 0, l + 1) : substring(url, 0, k));
			} else {							
				normalizedUrl = url;
			}
			if((k = normalizedUrl.indexOf(';')) != -1) {
				normalizedUrl = substring(normalizedUrl, 0, k);
			}
		} else if((k = url.indexOf(';')) != -1) {
			normalizedUrl = substring(url, 0, k);
		} else if((k = url.indexOf('#')) != -1) {
			// e.g.: http://www.networkbench.com/index.html#top
			normalizedUrl = substring(url, 0, k);
		} else {
			///TODO should or not permit some url like "http://a.b.com/c/d=f"?
			normalizedUrl = url;
		}
		
		return normalizedUrl;
	}
	
	private final static char[] HOST_URI_DELIMITERS = new char[] {'/', '?', '&', '#', ';'};
    private final static BitSet dontNeedEncoding;
    private final static BitSet dontNeedEncodingAscii;
    private final static int caseDiff = ('a' - 'A');
    
    static {
    	/* The list of characters that are not encoded has been
    	 * determined as follows:
    	 *
    	 * RFC 2396 states:
    	 * -----
    	 * Data characters that are allowed in a URI but do not have a
    	 * reserved purpose are called unreserved.  These include upper
    	 * and lower case letters, decimal digits, and a limited set of
    	 * punctuation marks and symbols. 
    	 *
    	 * unreserved  = alphanum | mark
    	 *
    	 * mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
    	 *
    	 * Unreserved characters can be escaped without changing the
    	 * semantics of the URI, but this should not be done unless the
    	 * URI is being used in a context that does not allow the
    	 * unescaped character to appear.
    	 * -----
    	 *
    	 * It appears that both Netscape and Internet Explorer escape
    	 * all special characters from this list with the exception
    	 * of "-", "_", ".", "*". While it is not clear why they are
    	 * escaping the other characters, perhaps it is safest to
    	 * assume that there might be contexts in which the others
    	 * are unsafe if not escaped. Therefore, we will use the same
    	 * list. It is also noteworthy that this is consistent with
    	 * O'Reilly's "HTML: The Definitive Guide" (page 164).
    	 *
    	 * As a last note, Intenet Explorer does not encode the "@"
    	 * character which is clearly not unreserved according to the
    	 * RFC. We are being consistent with the RFC in this matter,
    	 * as is Netscape.
    	 *
    	 */

		dontNeedEncoding = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding.set(i);
		}
		dontNeedEncoding.set(' '); /*
									 * encoding a space to a + is done in the
									 * encode() method
									 */
		dontNeedEncoding.set('-');
		dontNeedEncoding.set('_');
		dontNeedEncoding.set('.');
		dontNeedEncoding.set('*');
		
		dontNeedEncodingAscii = new BitSet(256);
		for(int j = 32; j<= 126; ++j) {
			dontNeedEncodingAscii.set(j);
		}
    }
	
	/**
	 * 将url分隔成host, uri数组
	 * @param url
	 * @return
	 */
	public static String[] splitHostAndUri(String url) {
		if(url == null || url.length() == 0)
			return null;
		
		int hostStartIndex = -1;
		if(url.startsWith("http://")) {
			hostStartIndex = 7;
		} else if(url.startsWith("https://")) {
			hostStartIndex = 8;
		}
		
		if(hostStartIndex != -1) {
			int delimiter = -1;
			for(int ci = hostStartIndex; ci < url.length() && delimiter == -1; ++ ci) {
				char c = url.charAt(ci);
				for(int i = 0; i < HOST_URI_DELIMITERS.length; ++i) {
					if(HOST_URI_DELIMITERS[i] == c) {
						delimiter = ci;
						break;
					}
				}
			}
			
			if(delimiter == -1) {
				// like www.networkbench.com
				return new String[] {url.substring(hostStartIndex), null};
			} else {
				return new String[] {url.substring(hostStartIndex, delimiter), url.substring(delimiter)};
			}
		} else {
			// non http or https
			return new String[] {null, url};
		}
	}
	
	public static String getNextToken(String str, char token, int beginIndex, int maxLength) {
		int endIndex = Math.min(str.length(), beginIndex + maxLength);
		for(int i = beginIndex; i < endIndex; ++i) {
			char c = str.charAt(i);
			if(c == token) {
				return beginIndex == i ? null : substring(str, beginIndex, i);
			}
		}
		
		return null;
	}
	
	public static String getNextToken(String str, char token, int beginIndex, int maxLength, boolean includeEOF) {
		int endIndex = Math.min(str.length(), beginIndex + maxLength);
		int i;
		for(i = beginIndex; i < endIndex; ++i) {
			char c = str.charAt(i);
			if(c == token) {
				return beginIndex == i ? null : substring(str, beginIndex, i);
			}
		}
		
		if(includeEOF && i == str.length()) {
			return substring(str, beginIndex);
		}
		
		return null;
	}
	
	public static String getNextToken(String str, char[] tokens, int beginIndex, int maxLength) {
		int endIndex = Math.min(str.length(), beginIndex + maxLength);
		for(int i = beginIndex; i < endIndex; ++i) {
			char c = str.charAt(i);
			for(int j = 0; j < tokens.length; ++j) {
				if(c == tokens[j]) {
					return beginIndex == i ? null : substring(str, beginIndex, i);
				}
			}
		}
		
		return null;
	}
	
	public static String getNextToken(String str, char[] tokens, int beginIndex, int maxLength, boolean includeEOF) {
		int endIndex = Math.min(str.length(), beginIndex + maxLength);
		int i;
		for(i = beginIndex; i < endIndex; ++i) {
			char c = str.charAt(i);
			for(int j = 0; j < tokens.length; ++j) {
				if(c == tokens[j]) {
					return beginIndex == i ? null : substring(str, beginIndex, i);
				}
			}
		}
		
		if(includeEOF && i == str.length()) {
			return substring(str, beginIndex);
		}
		
		return null;
	}
	
	public static boolean followWith(String str, char[] followingCharacters, int beginIndex) {
		int endIndex = beginIndex + followingCharacters.length;
		if(endIndex > str.length())
			return false;
		
		int k = 0;
		for(int i = beginIndex; i < endIndex; ++i, ++k) {
			if(str.charAt(i) != followingCharacters[k])
				return false;
		}
		
		return true;
	}
	
	public static int indexOfAny(String str, String[] needles) {
		for(int i = 0; i < needles.length; ++i) {
        	int k;
        	if((k = str.indexOf(needles[i])) != -1) {
        		return k;
        	}
        }
        
        return -1;
    }

    public static int indexOfAny(String str, String[] needles, int startIndex) {
        for(int i = 0; i < needles.length; ++i) {
        	int k;
        	if((k = str.indexOf(needles[i], startIndex)) != -1) {
        		return k;
        	}
        }
        
        return -1;
    }
    
	public static boolean startsWithAny(String str, String[] needles) {
		for(int i = 0; i < needles.length; ++i) {
        	if(str.startsWith(needles[i])) {
        		return true;
        	}
        }
        
        return false;
    }

	public static boolean startsWithAny(String str, String[] needles, int startIndex) {
		for(int i = 0; i < needles.length; ++i) {
        	if(str.length() > startIndex + needles[i].length()) {
        		boolean startsWith = true;
        		int k = startIndex;
        		for(int j = 0; j < needles[i].length(); ++j, ++k) {
        			if(needles[j].charAt(j) != str.charAt(k)) {
        				startsWith = false;
        				break;
        			}
        		}
        		
        		if(startsWith)
        			return true;
        	}
        }
        
        return false;
    }
    
    public static boolean startsWith(String str, char[] chars) {
    	if(str.length() < chars.length)
    		return false;
    	
    	for(int i = 0; i < chars.length; ++i) {
    		if(chars[i] != str.charAt(i))
    			return false;
    	}
    	
    	return true;
    }
    
    public static boolean endsWith(String str, char[] chars) {
    	if(str.length() < chars.length)
    		return false;
    	
    	for(int i = 0, j = str.length() - chars.length; i < chars.length; ++i, ++j) {
    		if(chars[i] != str.charAt(j))
    			return false;
    	}
    	
    	return true;
    }
    
	public static String normalizedLanguage(String language) {
		int startIndex = -1;
		int length = language.length();
		int endIndex = length;
		
		for(int i = 0; i < length; ++i) {
			switch(language.charAt(i)) {
			case ',' :
			case ';' :
				endIndex = i;
				return startIndex == -1 ? null : substring(language, startIndex, endIndex);
			case ' ' :
				if(startIndex != -1) {
					endIndex = i;
					return startIndex == -1 ? null : substring(language, startIndex, endIndex);
				}
				break;
			default :
				if(startIndex == -1) {
					startIndex = i;
				}
				break;
			}
		}
		
		return startIndex == -1 ? null : language;
	}
	
	public static String normalizedHost(String host) {
		if(host == null)
			return null;
		
		int length = host.length();
		int endIndex = length;
		int lastDotIndex = -1;
		
		for(int i = 0; i < length; ++i) {
			char c = host.charAt(i);
			if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '-') {
				//valid host chars
			} else if(c >= '0' && c <= '9') {
				//valid host (IP host?)
			} else if(c == '.') {
				lastDotIndex = i;
			} else if(c == ':') {
				//optional port start, discard
				endIndex = i;
				break;
			} else {
				//invalid host chars?
				return null;
			}
		}
		
		if(endIndex == 0 || lastDotIndex == -1 || lastDotIndex + 1 == endIndex) {
			return null;
		}
		
		return (endIndex == length ? host : substring(host, 0, endIndex));
	}
	
	/**
	 * 获取主域名
	 * @param host 域名
	 * @return 对应的一级域名，如www.baidu.com的主域名为baidu.com
	 */
	public static String getPrimaryDomainName (String host) {
		if(host == null || host.length() == 0)
			return null;
		
		String hostWithoutPort = host;
		int delimiter = hostWithoutPort.lastIndexOf(':');
		if(delimiter != -1) {
			hostWithoutPort = host.substring(0, delimiter);
		}
		
		if(IpUtils.isValidIp(hostWithoutPort)) {
			// raw IP
			return hostWithoutPort;
		}
		
		// 扫描最后域名3个分隔符（.）
		int[] delimiters = new int[3];
		int delimitersCount = 0;
		for(int i = hostWithoutPort.length() - 1; i >= 0; --i) {
			if(hostWithoutPort.charAt(i) == '.') {
				delimiters[delimitersCount] = i;
				++delimitersCount;
				if(delimitersCount == 3) {
					break;
				}
			}
		}
		
		String primaryDomainName;
		if(delimitersCount >= 2) {
			// 域名超过3段，检查是否为2段的后缀，如a.b.com.cn或a.com.cn等
			String secondarySuffix = hostWithoutPort.substring(delimiters[1] + 1, delimiters[0]);
			if("com".equals(secondarySuffix)
					|| "org".equals(secondarySuffix)
					|| "gov".equals(secondarySuffix)
					|| "net".equals(secondarySuffix)
					|| "co".equals(secondarySuffix)
					|| "edu".equals(secondarySuffix)) {
				// a.b.com.cn => b.com.cn, a.com.cn => a.com.cn
				primaryDomainName = (delimitersCount == 3 ? hostWithoutPort.substring(delimiters[2] + 1) : hostWithoutPort);
			} else {
				// a.b.c.com => c.com, a.b.com => b.com
				primaryDomainName = hostWithoutPort.substring(delimiters[1] + 1);
			}
		} else {
			// 域名只有一段，如a.com或a
			primaryDomainName = hostWithoutPort;
		}
		
		return primaryDomainName;
	}
	
    public static String substring(String str, int beginIndex) {
    	if(str == null)
    		return null;
    	
    	return substring(str, beginIndex, str.length());
    }
    
    public static String substring(String str, int beginIndex, int endIndex) {
    	if(str == null)
    		return null;
    	
    	if(endIndex == -1) {
    		endIndex = str.length();
    	}
    	
    	char[] substrChars = new char[endIndex - beginIndex];
    	str.getChars(beginIndex, endIndex, substrChars, 0);
    	return new String(substrChars);
    }
    
	public static String encodeURL(String s){
		return encodeURL(s, "UTF-8");
	}
	
	public static String encodeURL(String s, String encoding) {
		return encodeURL(s, encoding, false);
	}
	
	public static String encodeURL(String s, String encoding, boolean dontEncodeAsciiChars){
		if(s == null || s.equals("")){
			return s;
		}
		
		/*
		try {
			return URLEncoder.encode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		*/
		// 以下代码拷贝自  java.net.URLEncoder#encode()
		// 增加 dontEncodeAsciiChars 选项的支持，即只encode非ASCII字符
		final BitSet dontNeedEncoding = (dontEncodeAsciiChars ? StringUtils.dontNeedEncodingAscii : StringUtils.dontNeedEncoding);
		boolean needToChange = false;
		StringBuffer out = new StringBuffer(s.length());
		Charset charset;
		CharArrayWriter charArrayWriter = new CharArrayWriter();

		if (encoding == null)
			encoding = "UTF-8";

		try {
			charset = Charset.forName(encoding);
		} catch (IllegalCharsetNameException e) {
			return null;
		} catch (UnsupportedCharsetException e) {
			return null;
		}

		for (int i = 0; i < s.length();) {
			int c = (int) s.charAt(i);
			// System.out.println("Examining character: " + c);
			if (dontNeedEncoding.get(c)) {
				if (!dontEncodeAsciiChars && c == ' ') {
					c = '+';
					needToChange = true;
				}
				// System.out.println("Storing: " + c);
				out.append((char) c);
				i++;
			} else {
				// convert to external encoding before hex conversion
				do {
					charArrayWriter.write(c);
					/*
					 * If this character represents the start of a Unicode
					 * surrogate pair, then pass in two characters. It's not
					 * clear what should be done if a bytes reserved in the
					 * surrogate pairs range occurs outside of a legal surrogate
					 * pair. For now, just treat it as if it were any other
					 * character.
					 */
					if (c >= 0xD800 && c <= 0xDBFF) {
						/*
						 * System.out.println(Integer.toHexString(c) +
						 * " is high surrogate");
						 */
						if ((i + 1) < s.length()) {
							int d = (int) s.charAt(i + 1);
							/*
							 * System.out.println("\tExamining " +
							 * Integer.toHexString(d));
							 */
							if (d >= 0xDC00 && d <= 0xDFFF) {
								/*
								 * System.out.println("\t" +
								 * Integer.toHexString(d) +
								 * " is low surrogate");
								 */
								charArrayWriter.write(d);
								i++;
							}
						}
					}
					i++;
				} while (i < s.length() && !dontNeedEncoding.get((c = (int) s.charAt(i))));

				charArrayWriter.flush();
				String str = new String(charArrayWriter.toCharArray());
				byte[] ba = str.getBytes(charset);
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
					// converting to use uppercase letter as part of
					// the hex value if ch is a letter.
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
					ch = Character.forDigit(ba[j] & 0xF, 16);
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
				}
				charArrayWriter.reset();
				needToChange = true;
			}
		}

		return (needToChange ? out.toString() : s);
	}
}
