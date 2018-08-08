package com.rinbo.zookeeper;

import org.apache.jute.*;
import org.apache.zookeeper.server.ByteBufferInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class JuteDemo {
    private static class MockReqRecord implements Record {
        private long sessionId;
        private String type;

        public MockReqRecord() {
        }

        public MockReqRecord(long sessionId, String type) {
            this.sessionId = sessionId;
            this.type = type;
        }

        public long getSessionId() {
            return sessionId;
        }

        public void setSessionId(long sessionId) {
            this.sessionId = sessionId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public void serialize(OutputArchive archive, String tag) throws IOException {
            archive.startRecord(this, tag);
            archive.writeLong(sessionId, "sessionId");
            archive.writeString(type, "type");
            archive.endRecord(this, tag);
        }

        @Override
        public void deserialize(InputArchive archive, String tag) throws IOException {
            archive.startRecord(tag);
            sessionId = archive.readLong("sessionId");
            type = archive.readString("type");
            archive.endRecord(tag);
        }

        @Override
        public String toString() {
            return "MockReqRecord{" +
                    "sessionId=" + sessionId +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryOutputArchive archive = BinaryOutputArchive.getArchive(out);
        //序列化
        new MockReqRecord(23122342L,"xxx").serialize(archive,"header");
        archive.writeInt(10,"len");
        archive.writeInt(102,"lens");
        ByteBuffer buffer = ByteBuffer.wrap(out.toByteArray());

        //反序列化
        ByteBufferInputStream in = new ByteBufferInputStream(buffer);
        BinaryInputArchive bia = BinaryInputArchive.getArchive(in);
        MockReqRecord m = new MockReqRecord();
        m.deserialize(bia,"header");
        System.out.println(bia.readInt("lens"));
        System.out.println(bia.readInt("lens"));
        System.out.println(m);
        out.close();
        in.close();
    }
}
