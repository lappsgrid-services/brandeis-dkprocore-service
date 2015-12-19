{

    def targetText = %._(&:Sofa.@sofaString)
    def targetAnnotations = []

    targetAnnotations += &:Sentence.foreach {
          def targetId = %._(&."@xmi:id")
          def targetBegin = %._(&.@begin).toInteger()
          def targetEnd = %._(&.@end).toInteger()
          [
            id: targetId,
            start: targetBegin,
            end:  targetEnd,
            "@type":  "http://vocab.lappsgrid.org/Sentence",
            features: [
                sentence: targetText.substring(targetBegin, targetEnd)
            ]
          ]
    }


    targetAnnotations += &:Token.foreach {
          def targetId = %._(&."@xmi:id")
          def targetBegin = %._(&.@begin).toInteger()
          def targetEnd = %._(&.@end).toInteger()
          [
            id: targetId,
            start: targetBegin,
            end:  targetEnd,
            "@type":  "http://vocab.lappsgrid.org/Token",
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
                          producer  "edu.brandeis.cs.lappsgrid.stanford.corenlp.POSTagger:2.0.1-SNAPSHOT"
                          type  "tagger:stanford"
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