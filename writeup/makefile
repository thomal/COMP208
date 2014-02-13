pdf : project.tex text/*/*.tex
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
