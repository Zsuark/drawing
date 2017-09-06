(ns drawing.fibonacci)

(defn countFibStack [n]
	{:pre [(integer? n) (pos? n)]}
	(loop [ counter 0 remaining [ n ] ]
		(if (empty? remaining) counter
			(let [ head (first remaining) tail (rest remaining)
				newCount (inc' counter) ]
				(case head
					2 (recur newCount tail)
					1 (recur newCount tail)
					(let [ a (- head 1) b (- head 2) newTail (conj tail a b) ]
						(recur newCount newTail)))))))



; (defn fibRec [n]
; 	{:pre [(integer? n) (pos? n)]}
; 	(case n
; 		2 1
; 		1 1
; 		(+ (fibRec (- n 1)) (fibRec (- n 2)))))
