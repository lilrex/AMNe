package com.lilrex.amne.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.err.println("STATUS: " + response.status());
            System.err.println("VERSION: " + response.protocolVersion());
            System.err.println();

            if(!response.headers().isEmpty()) {
                for(CharSequence name: response.headers().names()) {
                    for(CharSequence value: response.headers().getAll(name)) {
                        System.err.println("HEADER: " + name + " = " + value);
                    }
                }
                System.err.println();
            }
        }

        if(msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            System.err.println(content.content().toString(CharsetUtil.UTF_8));
            System.err.flush();

            if(content instanceof LastHttpContent) {
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
