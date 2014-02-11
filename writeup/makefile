pdf : project.tex text/*/*.tex
	pdflatex project.tex
	pdflatex project.tex #toc and todo
	bibtex project
	pdflatex project.tex #references
	bibtex project
	pdflatex project.tex
	make clean
clean : project.log project.aux project.toc project.tdo project.bbl project.blg project-blx.bib
	rm project.log
	rm project.aux
	rm project.toc
	rm project.tdo
	rm project.bbl
	rm project.blg
	rm project-blx.bib
