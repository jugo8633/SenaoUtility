package com.senao.utility;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import javax.xml.parsers.*;

public class XmlParser
{
	public XmlParser()
	{

	}

	public NodeList getNodeList(final String strXML, final String strElement)
	{
		try
		{
			InputStream is = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setCoalescing(true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(strElement);
			is = null;
			return nList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Node getNode(String tagName, NodeList nodes) throws Exception
	{
		for (int x = 0; x < nodes.getLength(); x++)
		{
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName))
			{
				return node;
			}
		}

		return null;
	}

	public String getNodeValue(Node node) throws Exception
	{
		NodeList childNodes = node.getChildNodes();
		for (int x = 0; x < childNodes.getLength(); x++)
		{
			Node data = childNodes.item(x);
			if (data.getNodeType() == Node.TEXT_NODE)
				return data.getNodeValue();
		}
		return "";
	}

	public String getNodeValue(String tagName, NodeList nodes) throws Exception
	{
		for (int x = 0; x < nodes.getLength(); x++)
		{
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName))
			{
				NodeList childNodes = node.getChildNodes();
				for (int y = 0; y < childNodes.getLength(); y++)
				{
					Node data = childNodes.item(y);
					if (data.getNodeType() == Node.TEXT_NODE)
						return data.getNodeValue();
				}
			}
		}
		return "";
	}

	public String getNodeAttr(String attrName, Node node) throws Exception
	{
		NamedNodeMap attrs = node.getAttributes();
		for (int y = 0; y < attrs.getLength(); y++)
		{
			Node attr = attrs.item(y);
			if (attr.getNodeName().equalsIgnoreCase(attrName))
			{
				return attr.getNodeValue();
			}
		}
		return "";
	}

	public String getNodeAttr(String tagName, String attrName, NodeList nodes) throws Exception
	{
		for (int x = 0; x < nodes.getLength(); x++)
		{
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName))
			{
				NodeList childNodes = node.getChildNodes();
				for (int y = 0; y < childNodes.getLength(); y++)
				{
					Node data = childNodes.item(y);
					if (data.getNodeType() == Node.ATTRIBUTE_NODE)
					{
						if (data.getNodeName().equalsIgnoreCase(attrName))
							return data.getNodeValue();
					}
				}
			}
		}

		return "";
	}

	/** Get Tag value from XML **/
	public String getNodeValue(final String strXML, final String strTagPathName) throws Exception
	{
		String strValue = null;
		if (null != strXML && 0 < strXML.trim().length())
		{
			try
			{
				InputStream stream = new ByteArrayInputStream(strXML.getBytes(StandardCharsets.UTF_8));
				InputStreamReader file = new InputStreamReader(stream, "UTF-8");
				org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
				org.dom4j.Document document = reader.read(file);
				if (null != document )
				{
					document.normalize();
					List<?> entity_list = null;
					Iterator<?> i_entity = null;
					entity_list = document.selectNodes(strTagPathName);
					if (null != entity_list)
					{
						i_entity = entity_list.iterator();
						if (i_entity.hasNext())
						{
							org.dom4j.Element v_element = (org.dom4j.Element) i_entity.next();
							strValue = v_element.getText();
						}
					}
				}
				reader = null;
				file.close();
				file = null;
				stream.close();
				stream = null;
			}
			catch (Exception e)
			{
				throw new Exception("XmlParser Exception:" + e.toString());
			}
		}

		return strValue.trim();
	}
}
