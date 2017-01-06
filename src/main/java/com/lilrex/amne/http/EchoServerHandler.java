package com.lilrex.amne.http;

import com.lilrex.amne.worker.IMsgSender;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

/**
 * The parent of all handlers: a simple server handler that echoes the incoming request
 */
public class EchoServerHandler extends MsgSenderServerHandler {

    public EchoServerHandler(IMsgSender sender) {
        super(sender);
    }

    @Override
    protected void readHttpRequest(HttpRequest request) {
        buf.setLength(0);
        buf.append("WELCOME TO THE HTTP WEB SERVER\r\n");
        buf.append("==============================\r\n");
        buf.append("VERSION: ").append(request.protocolVersion()).append("\r\n");
        buf.append("HOSTNAME: ").append(request.headers().get(HttpHeaderNames.HOST, "unknown")).append("\r\n");
        buf.append("REQUEST_URI: ").append(request.uri()).append("\r\n\r\n");
        HttpHeaders headers = request.headers();
        if(!headers.isEmpty()) {
            for(Map.Entry<String, String> h: headers) {
                CharSequence key = h.getKey();
                CharSequence value = h.getValue();
                buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
            }
            buf.append("\r\n");
        }

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if(!params.isEmpty()) {
            for(Map.Entry<String, List<String>> p: params.entrySet()) {
                String key = p.getKey();
                List<String> values = p.getValue();
                for(String val : values) {
                    buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
                }
            }
            buf.append("\r\n");
        }

        appendDecoderResult(buf, request);
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.decoderResult();
        if(result.isSuccess()) {
            return;
        }
        buf.append(".. WITH DECODER FAILURE: ");
        buf.append(result.cause());
        buf.append("\r\n");
    }

    @Override
    protected void readHttpContent(HttpContent httpContent) {
        ByteBuf content = httpContent.content();
        if(content.isReadable()) {
            buf.append("CONTENT: ");
            buf.append(content.toString(CharsetUtil.UTF_8));
            buf.append("\r\n");
            appendDecoderResult(buf, request);
        }

        if(httpContent instanceof LastHttpContent) {
            LastHttpContent trailer = (LastHttpContent) httpContent;
            if(!trailer.trailingHeaders().isEmpty()) {
                buf.append("\r\n");
                for (CharSequence name: trailer.trailingHeaders().names()) {
                    for (CharSequence value: trailer.trailingHeaders().getAll(name)) {
                        buf.append("TRAILING HEADER: ");
                        buf.append(name).append(" = ").append(value).append("\r\n");
                    }
                }
                buf.append("\r\n");
            }
        }
    }

    @Override
    protected byte[] processMessage(byte[] message) {
//        msgSender.send(message);
        return message;
    }
}
