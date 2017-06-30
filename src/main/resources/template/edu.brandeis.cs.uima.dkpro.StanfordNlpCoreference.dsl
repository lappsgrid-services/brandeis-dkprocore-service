{

    def targetText = %.s_(&:Sofa.@sofaString)
    
    
    def totchains = 0
    def totmarkables = 0

    def chains = &:CoreferenceChain.collectEntries {
        [it.@first.toInteger(), totchains++]
    }
    
    &:CoreferenceLink.each {
        def markableId =  %.i_(&."@xmi:id")
        if (chains.containsKey(markableId) && it.@next != null && it.@next!= "") {
            chains[it.@next.toInteger()] = chains[markableId]
        }
    }

    def tokIdx = 0

    def tokStartIdx = [:]
    def tokEndIdx = [:]

    def chainsInversed = [:]
    if (totchains > 0) {
        chainsInversed = (0..totchains-1).collectEntries { [it, []] }
        chains.keySet().each {
            chainsInversed[chains[it]] += it
        }
    }
    
    def targetAnnotations = []

    targetAnnotations += &:Token.foreach {
          def targetBegin = %.i_(&.@begin)
          def targetEnd = %.i_(&.@end)
          tokStartIdx[targetBegin] = tokIdx
          tokEndIdx[targetEnd] = tokIdx
          def targetId = "tok_" + tokIdx++
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
    
    targetAnnotations += &:CoreferenceLink.foreach {
        def markableId =  "mrk_" + %.s_(&."@xmi:id")
        def targetBegin = %.i_(&.@begin)
        def targetEnd = %.i_(&.@end)
        def targets = (tokStartIdx[targetBegin]..tokEndIdx[targetEnd]).collect {
            "tok_" + it
        }
        [
            id: markableId, 
            "@type":  "http://vocab.lappsgrid.org/Markable",
            start: targetBegin,
            end:  targetEnd,
            features: [
                targets: targets
            ]
        ]
    }

    targetAnnotations += chainsInversed.keySet().collect {
        def corefId = "coref_" + it
        def mentions = chainsInversed[it].collect {
            "mrk_" + it }
        def representative = chainsInversed[it][0]
        [
            id: corefId,
            "@type":  "http://vocab.lappsgrid.org/Coreference",
            features: [
                mentions: mentions, 
                representative: representative
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
                          producer  "edu.brandeis.cs.uima.dkpro.stanford.StanfordNlpCoreference:0.0.1-SNAPSHOT"
                          type  "coref:dkpro_stanford"
                      }
                      "http://vocab.lappsgrid.org/Markable" {
                          producer  "edu.brandeis.cs.uima.dkpro.stanford.StanfordNlpCoreference:0.0.1-SNAPSHOT"
                          type  "coref:dkpro_stanford"
                      }
                      "http://vocab.lappsgrid.org/Corefernce" {
                          producer  "edu.brandeis.cs.uima.dkpro.stanford.StanfordNlpCoreference:0.0.1-SNAPSHOT"
                          type  "coref:dkpro_stanford"
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
