package C6_Task_Execution.Ch6_10_Rendering_page_elements_sequentially;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.Ch6_10_Rendering_page_elements_sequentially.SingleThreadRenderer.ImageData;
import C6_Task_Execution.Ch6_10_Rendering_page_elements_sequentially.SingleThreadRenderer.ImageInfo;
import support.sleep.Sleep;

public class SingleThreadRendererTest {

    @Test
    public void justRunIt() {
        SingleThreadRenderer renderer = new SingleThreadRendererImpl();
        long startTime = System.nanoTime();
        renderer.renderPage( "a page" );
        long endTime = System.nanoTime();
        System.out.println( "finished within " + (endTime - startTime) + " nano seconds" );
    }

    class SingleThreadRendererImpl extends SingleThreadRenderer {

        @Override
        void renderText(CharSequence s) {
            Sleep.sleepUninterruptibly( 50, TimeUnit.MILLISECONDS );
            System.out.println( "render text" );
        }

        @Override
        List< ImageInfo > scanForImageInfo(CharSequence s) {
            Sleep.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
            System.out.println( "scan for image info" );

            List< ImageInfo > imageList = new ArrayList< ImageInfo >();
            for (int i = 0; i < 8; i++)
                imageList.add( new ImageInfoImpl() );
            return imageList;
        }

        @Override
        void renderImage(ImageData i) {
            Sleep.sleepUninterruptibly( 500, TimeUnit.MILLISECONDS );
            System.out.println( "render image" );
        }

    }

    class ImageInfoImpl implements ImageInfo {

        @Override
        public ImageData downloadImage() {
            Sleep.sleepUninterruptibly( 800, TimeUnit.MILLISECONDS );
            System.out.println( "download image" );
            return new ImageDataImpl();
        }

    }

    class ImageDataImpl implements ImageData {

    }

}
