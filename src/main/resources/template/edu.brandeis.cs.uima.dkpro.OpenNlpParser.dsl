{

    def genPentree

    genPentree = { id ->
        def node = &:"*".findAll{ &."@xmi:id" == id }[0]
        def targetConstituentType = node."@constituentType"
        def res = ""
        if (targetConstituentType == "") {
            def targetId = %.s_(node."@xmi:id")
            def targetBegin = %.i_(node.@begin)
            def pos = %.s_(node.@pos)
            def targetPosTag = &:"*".findAll{ &."@xmi:id" == pos }.foreach{%.s_(&.@PosValue)}[0]
            res = "(" + targetPosTag +" " + %.s_(&:Sofa.@sofaString).substring(targetBegin, targetBegin) +" )"
        } else {
            def targetChildren =  %.s_(node.@children)
            def subtree = ""
            targetChildren.split("\\s").foreach {
                subtree += genPentree.trampoline(it) +" \r\n"
            }
            res = "(" +targetConstituentType+ "\r\n   " + subtree + " )"
        }
        res
    }.trampoline()


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

    targetAnnotations += &:ROOT.foreach {
          def targetId = %.s_(&."@xmi:id")
          def targetBegin = %.i_(&.@begin)
          def targetEnd = %.i_(&.@end)
          def targetSentence = targetText.substring(targetBegin, targetEnd)
          def targetConstituents = &:"*".findAll{ &."@constituentType"!="" && %.i_(&.@begin) >= targetBegin && %.i_(&.@end) <= targetEnd}.foreach{%.s_(&."@xmi:id")}
         [
            id: targetId,
            start: targetBegin,
            end:  targetEnd,
            "@type":  "http://vocab.lappsgrid.org/PhraseStructure",
            features: [
                sentence: targetSentence,
                penntree: genPentree(targetId),
                constituents: (targetConstituents)
            ]
          ]
    }

    targetAnnotations += &:"*".findAll{ &."@constituentType"!=""}.foreach {
          def targetId = %.s_(&."@xmi:id")
          def targetBegin = %.i_(&.@begin)
          def targetEnd = %.i_(&.@end)
          def targetSentence = targetText.substring(targetBegin, targetEnd)
          def children = %.s_(&.@children).split("\\s")
          def tagetLabel = %.s_(&.@constituentType)
         [
            id: targetId,
            start: targetBegin,
            end:  targetEnd,
            "@type":  "http://vocab.lappsgrid.org/Constituent",
            label : tagetLabel,
            features: [
                children: (children)
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
                  contains  {
                    "http://vocab.lappsgrid.org/Token"  {
                      producer "eedu.brandeis.cs.uima.dkpro.OpenNlpParser:0.0.1-SNAPSHOT"
                      type  "parser:dkpro_opennlp"
                    }
                    "http://vocab.lappsgrid.org/PhraseStructure"   {
                      producer "edu.brandeis.cs.uima.dkpro.OpenNlpParser:0.0.1-SNAPSHOT"
                      type  "parser:dkpro_opennlp"
                    }
                    "http://vocab.lappsgrid.org/Constituent"   {
                      producer "edu.brandeis.cs.uima.dkpro.OpenNlpParser:0.0.1-SNAPSHOT"
                      type  "parser:dkpro_opennlp"
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