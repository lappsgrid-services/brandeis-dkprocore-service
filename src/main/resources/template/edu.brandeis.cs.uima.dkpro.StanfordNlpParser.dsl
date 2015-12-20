{

    def targetText = %.s_(&:Sofa.@sofaString)
    def targetAnnotations = []

    targetAnnotations += &:Sentence.foreach {
          def targetId = %.s_(&."@xmi:id")
          def targetBegin = %.i_(&.@begin)
          def targetEnd = %.i_(&.@end)
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
          def targetId = %.s_(&."@xmi:id")
          def targetBegin = %.i_(&.@begin)
          def targetEnd = %.i_(&.@end)
          def pos = %.s_(&.@pos)
          def targetPosTag = &:"*".findAll{ &."@xmi:id" == pos }.foreach{%.s_(&.@PosValue)}[0]
          [
            id: targetId,
            start: targetBegin,
            end:  targetEnd,
            "@type":  "http://vocab.lappsgrid.org/Token",
            features: [
                word: targetText.substring(targetBegin, targetEnd),
                pos: (targetPosTag)
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