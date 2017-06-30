{

    def targetText = %.s_(&:Sofa.@sofaString)
    def targetAnnotations = []

    targetAnnotations += &:"*".findAll{&.name()=="Person" || &.name()=="Organization"||&.name()=="Date"||&.name()=="Location"}.foreach {
          def targetId = %.s_(&."@xmi:id")
          def targetBegin = %.i_(&.@begin)
          def targetEnd = %.i_(&.@end)
          def name = &.name()
          def targetValue = %.toCapital(%.s_(&.@value))
          [
            id: targetId,
            start: targetBegin,
            end:  targetEnd,
            "@type":  "http://vocab.lappsgrid.org/"+%.toCapital(name),
            features: [
                word: targetText.substring(targetBegin, targetEnd)
            ]
          ]
    }


    discriminator  "http://vocab.lappsgrid.org/ns/media/jsonld"

    payload  {
        "@context" "http://vocab.lappsgrid.org/context-1.0.0.jsonld"

        metadata  {

        }

        text {
          "@value" targetText
        }

        views ([
            {
                metadata {
                    contains {
                      "http://vocab.lappsgrid.org/Token#pos" {
                          producer  "edu.brandeis.cs.uima.dkpro.stanford.StanfordNlpNamedEntityRecognizer:0.0.1-SNAPSHOT"
                          type  "ner:dkpro_stanford"
                      }
                    }
                }


                annotations  (
                    targetAnnotations
                )

            }
        ])
    }

}