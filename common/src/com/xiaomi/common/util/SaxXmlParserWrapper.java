package com.xiaomi.common.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.DOMException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public final class SaxXmlParserWrapper {
    /**
     * 利用传入的SAX DefaultHandler对XML InputStream进行解析,
     * 解析结果应该存放在DefaultHandler的具体实现里。
     * @param inputStream 对象对应的XML InputStream
     * @param saxHandler 对应的SAX XML handler
     * @throws ConfigParserException XML内容无法被解析
     */
    public static void parse(InputStream inputStream, DefaultHandler saxHandler)
        throws ConfigParserException
    {
        if (inputStream == null)
            throw new IllegalArgumentException("inputStream");

        parse(new InputSource(inputStream), saxHandler);
    }

    /**
     * 利用传入的SAX DefaultHandler对XML InputStream进行解析,
     * 解析结果应该存放在DefaultHandler的具体实现里。
     * @param inputStream 对象对应的XML InputStream
     * @param saxHandler 对应的SAX XML handler
     * @throws ConfigParserException XML内容无法被解析
     */
    public static void parse(InputSource inputSource, DefaultHandler saxHandler)
        throws ConfigParserException
    {
        if (inputSource == null)
            throw new IllegalArgumentException("inputStream");
        if (saxHandler == null)
            throw new IllegalArgumentException("saxHandler");

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();
            xmlreader.setContentHandler(saxHandler);

            xmlreader.parse(inputSource);
        } catch (IOException e) {
            throw new ConfigParserException("无法解析该XML文档.", e);
        } catch (SAXException e) {
            throw new ConfigParserException("无法解析该XML文档.", e);
        } catch (ParserConfigurationException e) {
            throw new ConfigParserException("无法解析该XML文档.", e);
        } catch (DOMException e) {
            throw new ConfigParserException("无法解析该XML文档.", e);
        }
    }

    public static final class ConfigParserException extends SAXException
    {
        /**
         * @param detailMessage
         */
        public ConfigParserException(String detailMessage) {
            super(detailMessage);
        }

        /**
         * @param detailMessage
         */
        public ConfigParserException(String detailMessage, Exception e) {
            super(detailMessage + " 详细错误: " + e.toString());
        }

        private static final long serialVersionUID = 2L;
    }
}
