package com.lilrex.amne.http;

import com.lilrex.amne.worker.IMsgSender;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class AMNeServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    private IMsgSender msgSender;

    public AMNeServer(IMsgSender msgSender) {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        this.msgSender = msgSender;
    }

    private void stopGracefully() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        System.out.println("Server is shut down");
    }

    public void doStart(int port) {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new AMNeServerChannelInitializer(msgSender));
            b.option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture f = b.bind(port).sync();
            serverChannel = f.channel();
            System.out.println("Server start OK!");
        } catch (Exception ex) {
            System.err.println("Server start error: " + ex.getMessage());
            stopGracefully();
        }
    }

    public void doStop() {
        stopGracefully();
    }
}
