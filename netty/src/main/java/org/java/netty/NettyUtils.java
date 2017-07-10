package org.java.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.java.utils.StringUtils;

/**
 * Created by msamoylych on 07.07.2017.
 */
public class NettyUtils {

    public static ByteBuf toByteBuf(ChannelHandlerContext ctx, String content) {
        byte[] bytes = StringUtils.getBytes(content);
        int l = bytes.length;
        ByteBuf buf = ctx.alloc().buffer(l, l);
        buf.writeBytes(bytes);
        return buf;
    }

    public static String toString(ByteBuf buf) {
        return buf.toString(StringUtils.DEFAULT_CHARSET);
    }
}
