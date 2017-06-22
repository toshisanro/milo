/*
 * Copyright (c) 2016 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.milo.opcua.stack.core.serialization;

import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import org.eclipse.milo.opcua.stack.core.serialization.codec.DataTypeCodec;
import org.eclipse.milo.opcua.stack.core.serialization.codec.DataTypeDictionary;
import org.eclipse.milo.opcua.stack.core.serialization.codec.DataTypeManager;
import org.eclipse.milo.opcua.stack.core.serialization.codec.OpcBinaryDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.serialization.codec.OpcXmlDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class OpcUaDataTypeManager implements DataTypeManager {

    public static final String BINARY_NAMESPACE_URI = "http://opcfoundation.org/UA/";
    public static final String XML_NAMESPACE_URI = "http://opcfoundation.org/UA/2008/02/Types.xsd";

    public static OpcUaDataTypeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final OpcUaDataTypeManager INSTANCE = new OpcUaDataTypeManager();
    }

    private final ConcurrentMap<String, DataTypeDictionary<?>> dictionaries = Maps.newConcurrentMap();
    private final ConcurrentMap<NodeId, DataTypeCodec> codecs = Maps.newConcurrentMap();

    private OpcUaDataTypeManager() {
        registerTypeDictionary(BuiltinDataTypeDictionary.getBinaryInstance());
        registerTypeDictionary(BuiltinDataTypeDictionary.getXmlInstance());
    }

    @Override
    public void registerTypeDictionary(DataTypeDictionary<?> dataTypeDictionary) {
        dictionaries.put(dataTypeDictionary.getNamespaceUri(), dataTypeDictionary);

        this.codecs.putAll(dataTypeDictionary.getCodecsByEncodingId());
    }

    @Nullable
    @Override
    public DataTypeDictionary getTypeDictionary(String namespaceUri) {
        return dictionaries.get(namespaceUri);
    }

    @Nullable
    @Override
    public DataTypeCodec getCodec(NodeId encodingId) {
        return codecs.get(encodingId);
    }

    @Nullable
    @Override
    public OpcBinaryDataTypeCodec<?> getBinaryCodec(NodeId encodingId) {
        DataTypeCodec codec = codecs.get(encodingId);

        if (codec instanceof OpcBinaryDataTypeCodec) {
            return (OpcBinaryDataTypeCodec) codec;
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public OpcXmlDataTypeCodec<?> getXmlCodec(NodeId encodingId) {
        DataTypeCodec codec = codecs.get(encodingId);

        if (codec instanceof OpcXmlDataTypeCodec) {
            return (OpcXmlDataTypeCodec) codec;
        } else {
            return null;
        }
    }

}
