package C6_Task_Execution.Ch6_13_Waiting_for_image_download_with_Future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.Ch6_13_Waiting_for_image_download_with_Future.FutureRenderer.ImageData;
import C6_Task_Execution.Ch6_13_Waiting_for_image_download_with_Future.FutureRenderer.ImageInfo;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class FutureRendererTest {

    @Test
    public void justRunIt() {
        FutureRenderer renderer = new FutureRendererImpl();
        long startTime = System.nanoTime();
        renderer.renderPage( "a page" );
        long endTime = System.nanoTime();
        System.out.println( "finished within " + (endTime - startTime) + " nano seconds" );
    }

    class FutureRendererImpl extends FutureRenderer {

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
