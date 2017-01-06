package com.lilrex.amne.http;

import com.lilrex.amne.worker.IMsgSender;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Borrowed snippets from http://netty.io/4.1/xref/io/netty/example/http/snoop/HttpSnoopServerHandler.html
 */
public abstract class MsgSenderServerHandler extends SimpleChannelInboundHandler<Object> {

    IMsgSender msgSender;

    protected HttpRequest request;
    protected StringBuilder buf = new StringBuilder();

    protected final static String RESPONSE_CONTENT_TYPE = "text/plain; charset=UTF=8";
    protected String responseContentType = RESPONSE_CONTENT_TYPE;

    public MsgSenderServerHandler(IMsgSender msgSender) {
        super();
        this.msgSender = msgSender;
    }


    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            if(HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            readHttpRequest(request);
        }

        if(msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent)msg;
            readHttpContent(httpContent);
            if(msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;
                readHttpContent(trailer);
                byte[] msgBytes = String.valueOf(buf).getBytes();
                byte[] response = processMessage(msgBytes);
                if(!writeResponse(trailer, ctx, response, responseContentType)) {
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }
        }

        // if message is byte array, it's passed in from a message receiver
        if(msg instanceof byte[]) {
            byte[] message = (byte[]) msg;
            byte[] response = processMessage(message);
            msgSender.send(response);
        }
    }

    protected abstract void readHttpRequest(HttpRequest request);

    protected abstract void readHttpContent(HttpContent content);

    protected abstract byte[] processMessage(byte[] message);

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx, byte[] content, String contentType){
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(content));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);

        if(keepAlive) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the response.
        ctx.write(response);
        return keepAlive;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
