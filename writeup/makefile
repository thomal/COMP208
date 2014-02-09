pdf : project.tex text/*/*.tex
	pdflatex project.tex
	pdflatex project.tex # because of the TOC and todo list
clean : project.log project.aux project.toc project.tdo
	rm project.log
	rm project.aux
	rm project.toc
	rm project.tdo
