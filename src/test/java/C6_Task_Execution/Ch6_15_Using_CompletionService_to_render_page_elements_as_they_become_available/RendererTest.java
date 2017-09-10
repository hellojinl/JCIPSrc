package C6_Task_Execution.Ch6_15_Using_CompletionService_to_render_page_elements_as_they_become_available;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.Ch6_15_Using_CompletionService_to_render_page_elements_as_they_become_available.Renderer.ImageData;
import C6_Task_Execution.Ch6_15_Using_CompletionService_to_render_page_elements_as_they_become_available.Renderer.ImageInfo;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class RendererTest {

    @Test
    public void justRunIt() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Renderer renderer = new RendererImpl( executor );
        long startTime = System.nanoTime();
        renderer.renderPage( "a page" );
        long endTime = System.nanoTime();
        System.out.println( "finished within " + (endTime - startTime) + " nano seconds" );
    }

    class RendererImpl extends Renderer {

        RendererImpl(ExecutorService executor) {
            super( executor );
        }

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
