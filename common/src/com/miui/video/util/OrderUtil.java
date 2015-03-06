package com.miui.video.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

public class OrderUtil {

	public interface NameComparable {
		public String getName();
		public void setHeadName(char c);
	}
	
	public static final String TAG = OrderUtil.class.getName();
		
	public static <T extends NameComparable> void orderItems(List<T> items) {
		if (items == null || items.size() > 1) {
			return;
		}
		Log.d(TAG, "sort " + items.size() + " items, start.");
		try {
			Collections.sort(items, new Comparator<T>() {
				@Override
				public int compare(T lhs, T rhs) {
					String lName = lhs.getName();
					String rName = rhs.getName();
					if (lName == null || lName.length() == 0 || rName == null || rName.length() == 0) {
						return compareWithNull(lName, rName);
					}
					lName = trim(lName);
					rName = trim(rName);
					boolean isLHeadNameSet = false;
					boolean isRHeadNameSet = false;
					while (lName.length() != 0 && rName.length() != 0) {
						StringBuilder lNumber = new StringBuilder();
						StringBuilder rNumber = new StringBuilder();
						int lIndex = indexNumber(lName, lNumber);
						int rIndex = indexNumber(rName, rNumber);
						String lKey = lName;
						String rKey = rName;
						if (lIndex > 0 && lIndex == rIndex) {
							lKey = lName.substring(0, lIndex);
							rKey = rName.substring(0, rIndex);
							if (lKey.toLowerCase().equals(rKey.toLowerCase())) {
								lName = lName.substring(lIndex);
								rName = rName.substring(rIndex);
								lIndex = 0;
								rIndex = 0;
								if (!isLHeadNameSet) {
									isLHeadNameSet = true;
									lhs.setHeadName(getHeadChar(lKey));
								}
								if (!isRHeadNameSet) {
									isRHeadNameSet = true;
									rhs.setHeadName(getHeadChar(rKey));
								}
							}else{
								if (isContainChinese(lKey)) {
									lKey = HanziToPinyin.getPinYin(lKey);
								}else{
									lKey = lKey.toLowerCase();
								}
								if (isContainChinese(rKey)) {
									rKey = HanziToPinyin.getPinYin(rKey);
								}else{
									rKey = rKey.toLowerCase();
								}
								if (!isLHeadNameSet) {
									isLHeadNameSet = true;
									lhs.setHeadName(lKey.charAt(0));
								}
								if (!isRHeadNameSet) {
									isRHeadNameSet = true;
									rhs.setHeadName(rKey.charAt(0));
								}
								return lKey.compareTo(rKey);
							}
						}
						if (lIndex == 0 && lIndex == rIndex) {
							if (!isLHeadNameSet) {
								isLHeadNameSet = true;
								lhs.setHeadName(lNumber.charAt(0));
							}
							if (!isRHeadNameSet) {
								isRHeadNameSet = true;
								rhs.setHeadName(rNumber.charAt(0));
							}
							String number1 = lNumber.toString();
							String number2 = rNumber.toString();
							int length1 = number1.length();
							int length2 = number2.length();
							if (length1 != length2) {
								return length1 - length2;
							}
							int comp = number1.compareTo(number2);
							if (comp != 0) {
								return comp;
							}else{
								if (lName.length() == length1 && 
										rName.length() != length2) {
									return -1;
								}else if (lName.length() != length1 && 
										rName.length() == length2) {
									return 1;
								}else if (lName.length() == length1 && 
										rName.length() == length2) {
									return 0;
								}
								lName = lName.substring(lNumber.length());
								rName = rName.substring(rNumber.length());
							}
						}else{
							if (isContainChinese(lKey)) {
								lKey = HanziToPinyin.getPinYin(lKey);
							}else{
								lKey = lKey.toLowerCase();
							}
							if (isContainChinese(rKey)) {
								rKey = HanziToPinyin.getPinYin(rKey);
							}else{
								rKey = rKey.toLowerCase();
							}
							if (!isLHeadNameSet) {
								isLHeadNameSet = true;
								lhs.setHeadName(lKey.charAt(0));
							}
							if (!isRHeadNameSet) {
								isRHeadNameSet = true;
								rhs.setHeadName(rKey.charAt(0));
							}
							return lKey.compareTo(rKey);
						}
					}
					if ((lName == null || lName.length() == 0) && 
							(rName != null && rName.length() != 0)) {
						return -1;
					}else if ((rName == null || rName.length() == 0) && 
							(lName != null && lName.length() != 0)) {
						return 1;
					}
					return 0;
				}
			});
			Log.d(TAG, "sort " + items.size() + " items, end.");
		} catch (Exception e) {
			Log.d(TAG, "sort failed.", e);
		}
	}

	
	private static String trim(String text) {
		if (text != null && text.length() > 1 && text.charAt(0) == '[') {
			text = text.substring(1);
		}
		return text;
	}
	
	private static int indexNumber(String text, StringBuilder builder) {
			int numberStart = -1;
			boolean allZeros = true;
			if (text != null) {
				for (int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					if (c >= '0' && c <= '9') {
						if (numberStart == -1) {
							numberStart = i;
						}
						if (c != '0' || !allZeros) {
							builder.append(c);
						}
						if (c != '0') {
							allZeros = false;
						}
					}else if (numberStart >= 0) {
						if (builder.length() == 0) {
							builder.append('0');
						}
						return numberStart;
					}
				}
			}
			if (numberStart >= 0 && builder.length() == 0) {
				builder.append('0');
			}
			return numberStart;
	}
	
	private static char getHeadChar(String text) {
		try {
			if (isContainChinese(text)) {
				text = HanziToPinyin.getPinYin(text);
			}
			return text.charAt(0);
		} catch (Exception e) {
		}
		return ' ';
	}
	
	private static int compareWithNull(String s1, String s2) {
		if (Util.isEmpty(s1)) {
			if (Util.isEmpty(s2)) {
				return 0;
			}
			return -1;
		} 
		if (Util.isEmpty(s2)) {
			return 1;
		}
		return s1.compareTo(s2);
	}
	
//	private static int compareObject(String o1, boolean isNumber1, String o2, boolean isNumber2) {
//		if (isNumber1) {
//			if (isNumber2) {
//				int i1 = o1.length(), i2 = o2.length();
//				if (i1 != i2) {
//					return i1 - i2;
//				}
//				return o1.compareTo(o2);
//			}
//			return -1;
//		}
//		if (isNumber2) {
//			return 1;
//		}
//		return (o1).compareTo(o2);
//	}
//  another implementation	
	/*private static class SplitNumber{
		public ArrayList<String> segments = new ArrayList<String>(126);
		public ArrayList<Boolean> isNumber = new ArrayList<Boolean>(126);
		public String substring;
	}
	public static List<MediaItem> orderMediaItems(List<MediaItem> mediaItems) {
		final HashMap<String, SplitNumber> sortHelper = new HashMap<String, SplitNumber>();

		Collections.sort(mediaItems, new Comparator<MediaItem>() {
			@Override
			public int compare(MediaItem lhs, MediaItem rhs) {
				SplitNumber lSplit;
				SplitNumber rSplit;
				if (sortHelper.containsKey(lhs.getName())) {
					lSplit = sortHelper.get(lhs.getName());
				}else{
					lSplit = new SplitNumber();
					lSplit.substring = trim(lhs.getName());
					sortHelper.put(lhs.getName(), lSplit);
				}
				if (sortHelper.containsKey(rhs.getName())) {
					rSplit = sortHelper.get(rhs.getName());
				}else{
					rSplit = new SplitNumber();
					rSplit.substring = trim(rhs.getName());
					sortHelper.put(rhs.getName(), rSplit);
				}
				ArrayList<String> lSegments = lSplit.segments;
				ArrayList<String> rSegments = rSplit.segments;
				ArrayList<Boolean> lIsNumber = lSplit.isNumber;
				ArrayList<Boolean> rIsNumber = rSplit.isNumber;
				int lCursor = 0, rCursor = 0;
				int ret = 0;
				while((lCursor < lSegments.size() || !Util.isEmpty(lSplit.substring)) &&
				(rCursor < rSegments.size() || !Util.isEmpty(rSplit.substring))) {
					String lSegment = null;
					String rSegment = null;
					if (lCursor < lSegments.size()) {
						lSegment = lSegments.get(lCursor);
					}else{
						StringBuilder number = new StringBuilder();
						int index = indexNumber(lSplit.substring, number);
						if (index > 0) {
							String headText = lSplit.substring.substring(0, index);
							if (isContainChinese(headText)) {
								headText = HanziToPinyin.getPinYin(headText);
							}else{
								headText = headText.toLowerCase();
							}
							lSegments.add(headText);
							lIsNumber.add(false);
							lSegment = headText;
							lSegments.add(number.toString());
							lIsNumber.add(true);
							lSplit.substring = lSplit.substring.substring(index + number.length());
						}else if (index == 0) {
							lSegment = number.toString();
							lSegments.add(lSegment);
							lIsNumber.add(true);
							lSplit.substring = lSplit.substring.substring(index + number.length());
						}else{
							String headText = lSplit.substring;
							if (isContainChinese(headText)) {
								headText = HanziToPinyin.getPinYin(headText);
							}else{
								headText = headText.toLowerCase();
							}
							lSegments.add(headText);
							lIsNumber.add(false);
							lSplit.substring = null;
							lSegment = headText;
						}
					}
					if (rCursor < rSegments.size()) {
						rSegment = rSegments.get(rCursor);
					}else{
						StringBuilder number = new StringBuilder();
						int index = indexNumber(rSplit.substring, number);
						if (index > 0) {
							String headText = rSplit.substring.substring(0, index);
							if (isContainChinese(headText)) {
								headText = HanziToPinyin.getPinYin(headText);
							}else{
								headText = headText.toLowerCase();
							}
							rSegment = headText;
							rSegments.add(headText);
							rIsNumber.add(false);
							rSegments.add(number.toString());
							rIsNumber.add(true);
							rSplit.substring = rSplit.substring.substring(index + number.length());
						}else if (index == 0) {
							rSegment = number.toString();
							rSegments.add(rSegment);
							rIsNumber.add(true);
							rSplit.substring = rSplit.substring.substring(index + number.length());
						}else{
							String headText = rSplit.substring;
							if (isContainChinese(headText)) {
								headText = HanziToPinyin.getPinYin(headText);
							}else{
								headText = headText.toLowerCase();
							}
							rSegments.add(headText);
							rIsNumber.add(false);
							rSplit.substring = null;
							rSegment = headText;
						}
					}
					ret = compareObject(lSegment, lIsNumber.get(lCursor) ,
							rSegment, rIsNumber.get(rCursor));
					if (ret != 0) {
						break;
					}
					lCursor++;rCursor++;
				}
				if (lSegments.size() > 0 && (lhs.getName() == null || lhs.getName().length() == 0)) {
					lhs.setHeadName((lSegments.get(0) + "").charAt(0));
				}
				if (rSegments.size() > 0 && (rhs.getName() == null || rhs.getName().length() == 0)) {
					rhs.setHeadName((rSegments.get(0) + "").charAt(0));
				}
				if (ret != 0) {
					return ret;
				}
				return compareWithNull(lSplit.substring, lSplit.substring);
			}
		});
		return mediaItems;
	}*/
	
	public static boolean isChinese(char c) { 

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c); 

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS 

                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS 

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A 

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION 

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION 

                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) { 

            return true; 

        } 
        return false; 
     } 
	 
	 public static boolean isContainChinese(String str) {
		 for (int i = 0; i < str.length(); i++) {
			 char c = str.charAt(i);
			 if (isChinese(c)) {
				 return true;
			 }
		 }
		 return false;
	 }
}
