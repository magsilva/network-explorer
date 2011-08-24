/*
Copyright (C) 2011 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ironiacorp.network.protocol.slp;

import java.util.ArrayList;
import java.util.List;

import com.ironiacorp.io.StreamUtil;


public class SLPServiceReplyMessage extends SLPMessage
{
	private static final int ERROR_CODE_OFFSET = 16;
	private static final int ERROR_CODE_LENGTH = 2;

	private static final int URL_ENTRY_COUNT_OFFSET = 18;
	private static final int URL_ENTRY_COUNT_LENGTH = 2;

	private static final int URL_ENTRIES_OFFSET = 20;
	
	private int errorCode;
	
	private List<SLPServiceURL> urls;

	private void processErrorCode(byte[] packet)
	{
		errorCode = StreamUtil.readInt(packet, ERROR_CODE_OFFSET, ERROR_CODE_LENGTH);
	}

	private void processUrlEntries(byte[] packet)
	{
		short urlEntryCount = (short) StreamUtil.readInt(packet, URL_ENTRY_COUNT_OFFSET, URL_ENTRY_COUNT_LENGTH);
		urls = new ArrayList<SLPServiceURL>(urlEntryCount);
		for (int i = 0, offset = URL_ENTRIES_OFFSET; i < urlEntryCount; i++) {
			SLPServiceURL url = new SLPServiceURL(packet, offset);
			offset += url.getLength();
		}
	}
	
	/**
	 * Get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * 
	 * <pre>
	 *      0                   1                   2                   3
	 *      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *     |        Service Location header (function = SrvRply = 2)       |
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *     |        Error Code             |        URL Entry count        |
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *     |       URL Entry 1          ...       URL Entry N              \
	 *     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
	@Override
	public void parse(byte[] packet)
	{
		super.parse(packet);
		processErrorCode(packet);
		processUrlEntries(packet);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public List<SLPServiceURL> getUrls()
	{
		return urls;
	}

	public void setUrls(List<SLPServiceURL> urls)
	{
		this.urls = urls;
	}
	
	public void addUrl(SLPServiceURL url)
	{
		urls.add(url);
	}
}
