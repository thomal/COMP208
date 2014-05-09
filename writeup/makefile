pdf :
	make -C .. doc
	cp ../doc/latex/refman.pdf refman.pdf
	make -C ../user_manual
	cp ../user_manual/manual.pdf manual.pdf
	pdflatex project.tex
	pdflatex project.tex #toc and todo
	bibtex project
	pdflatex project.tex #references
	bibtex project
	pdflatex project.tex
	make clean
	
clean :
	rm -f project.log
	rm -f project.aux
	rm -f project.toc
	rm -f project.tdo
	rm -f project.bbl
	rm -f project.blg
	rm -f project-blx.bib
	rm -f requirementsONLY.log
	rm -f requirementsONLY.aux
	rm -f requirementsONLY.toc
	rm -f requirementsONLY.tdo
	rm -f requirementsONLY.bbl
	rm -f requirementsONLY.blg
	rm -f requirementsONLY-blx.bib
	rm manual.pdf
	rm refman.pdf
	make -C ../user_manual clean
	make -C ../doc/latex clean
