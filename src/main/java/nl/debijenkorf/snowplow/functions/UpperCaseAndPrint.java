package nl.debijenkorf.snowplow.functions;

import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This function is an example how to add transformations to
 * Beam flows. Example usage:
 *
 *      Pipeline p = Pipeline.create(options);
 *      p.apply(ParDo.of(new UpperCaseAndPrint()))
 */
public class UpperCaseAndPrint extends DoFn<String, String> {

    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(UpperCaseAndPrint.class);

    @ProcessElement
    public void processElement(@Element String word, OutputReceiver<String> out) {
        logger.info(word);
        out.output(word.toUpperCase());
    }
}