/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.pt.mets.hadoop;

import eu.scape_project.pt.mets.hadoop.DTO;
import eu.scape_project.pt.mets.hadoop.MetsOutputFormat;
import eu.scape_project.pt.mets.hadoop.MetsRecordWriter;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.util.DefaultConverter;
import eu.scapeproject.util.ScapeMarshaller;
import gov.loc.mets.MetsType;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Matthias Rella, DME-AIT
 */
public class MetsRecordWriterTest {

    private FSDataOutputStream out;
    private Configuration conf;
    private Path path;
    
    public MetsRecordWriterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        conf = new Configuration();
        conf.set(MetsOutputFormat.TAG, "mets:mets");
        path = new Path("tmp" + System.currentTimeMillis() );
        FileSystem fs = path.getFileSystem(conf);
        out = fs.create(path);
    }
    
    @After
    public void tearDown() {
        try {
            FileSystem fs = path.getFileSystem(conf);
            fs.delete(path, true);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Test of write method, of class MetsRecordWriterTest.
     */
    @Ignore
    public void testWriteMetsType() throws Exception {
        DTO.setType(MetsType.class);

        String tag = conf.get(MetsOutputFormat.TAG);
        MetsRecordWriter writer = new MetsRecordWriter(out, tag);
        System.out.println("TEST: write");
        if( DTO.type.equals(MetsType.class)) {
            for( int i = 1; i <= 2; i++ ) {
                Text id = new Text("entity" + i );
                DTO dto = new DTO();
                InputStream in = this.getClass().getClassLoader()
                        .getResourceAsStream("metsDoc"+i+".xml");
                MetsType doc = (MetsType) ScapeMarshaller.newInstance().deserialize(in);
                
                dto.setObject(doc);
                writer.write(id, dto);
            }

            TaskAttemptContext context = new TestContext(conf, new TaskAttemptID());
            writer.close(context);

            InputStream in = this.getClass().getClassLoader()
                    .getResourceAsStream("metsdocs.xml");
            FileSystem fs = path.getFileSystem(conf);
            FSDataInputStream fsin = fs.open(path);
            int b = 0;
            StringBuilder exp = new StringBuilder();
            while(b != -1) {
                b = in.read();
                exp.append((char)b);
            }
            int f = 0;
            StringBuilder val = new StringBuilder();
            while(f != -1) {
                f = fsin.read();
                val.append((char)f);
            }

            String expected = exp.toString().replaceAll("\\s", "");
            String value = val.toString().replaceAll("\\s", "");

            System.out.println(expected);
            System.out.println(value);

            assertEquals(expected, value);
        }
        
    }
    /**
     * Test of write method, of class MetsRecordWriterTest.
     */
    @Test
    public void testWriteIntellectualEntity() throws Exception {
        DTO.setType(IntellectualEntity.class);

        String tag = conf.get(MetsOutputFormat.TAG);
        MetsRecordWriter writer = new MetsRecordWriter(out, tag);
        DefaultConverter conv = new DefaultConverter();
        System.out.println("TEST: write IntellectualEntity");
        if( DTO.type.equals(IntellectualEntity.class)) {
            for( int i = 1; i <= 2; i++ ) {
                Text id = new Text("entity" + i );
                DTO dto = new DTO();
                InputStream in = this.getClass().getClassLoader()
                        .getResourceAsStream("entity"+i+".xml");
                MetsType doc = 
                        (MetsType) ScapeMarshaller.newInstance().deserialize(in);
                IntellectualEntity ie = conv.convertMets(doc);
                
                dto.setObject(ie);
                writer.write(id, dto);
            }

            TaskAttemptContext context = new TestContext(conf, new TaskAttemptID());
            writer.close(context);

            InputStream in = this.getClass().getClassLoader()
                    .getResourceAsStream("entities.xml");
            FileSystem fs = path.getFileSystem(conf);
            FSDataInputStream fsin = fs.open(path);
            int b = 0;
            StringBuilder exp = new StringBuilder();
            while(b != -1) {
                b = in.read();
                exp.append((char)b);
            }
            int f = 0;
            StringBuilder val = new StringBuilder();
            while(f != -1) {
                f = fsin.read();
                val.append((char)f);
            }

            String expected = exp.toString().replaceAll("\\s", "");
            String value = val.toString().replaceAll("\\s", "");

            System.out.println(expected);
            System.out.println(value);

            assertEquals(expected, value);
        }
        
    }
}
