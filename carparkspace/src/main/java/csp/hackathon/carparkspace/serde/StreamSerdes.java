package csp.hackathon.carparkspace.serde;

import csp.hackathon.carparkspace.domain.BarrierEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public final class StreamSerdes {
    public static final Serde<BarrierEvent> barrierEventSerde = SerdeFactory.createPojoSerdeFor(BarrierEvent.class, false);
    public static final Serde<String> stringSerde = Serdes.String();
}
