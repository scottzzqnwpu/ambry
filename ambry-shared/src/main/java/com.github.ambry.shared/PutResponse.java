package com.github.ambry.shared;


import java.io.DataInputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.*;

/**
 * A Response to the Put Request
 */
public class PutResponse extends RequestOrResponse {

  private short error;
  private static final int Error_Size_InBytes = 2;

  public PutResponse(short versionId, int correlationId, short error) {
    super(RequestResponseType.PutReponse, versionId, correlationId);
    this.error = error;
  }

  public short getError() {
    return error;
  }

  public static PutResponse readFrom(DataInputStream stream) throws IOException {
    RequestResponseType type = RequestResponseType.values()[stream.readShort()];
    if (type != RequestResponseType.PutReponse) {
      throw new IllegalArgumentException("The type of request response is not compatible");
    }
    Short versionId  = stream.readShort();
    int correlationId = stream.readInt();
    Short error = stream.readShort();
    return new PutResponse(versionId, correlationId, error);
  }

  @Override
  public void writeTo(WritableByteChannel channel) throws IOException {
    if (bufferToSend == null) {
      bufferToSend = ByteBuffer.allocate((int)sizeInBytes());
      writeHeader();
      bufferToSend.putShort(error);
      bufferToSend.flip();
    }
    if (bufferToSend.remaining() > 0) {
      channel.write(bufferToSend);
    }
  }

  @Override
  public boolean isSendComplete() {
    return bufferToSend.remaining() == 0;
  }

  @Override
  public long sizeInBytes() {
    // header + error
    return super.sizeInBytes() + Error_Size_InBytes;
  }
}