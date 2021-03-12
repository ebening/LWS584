<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>

    <jsp:include page="template/head.jsp" />
    
</head>

<body>


	<div id="wrapper">

		<jsp:include page="template/nav_superior.jsp" />

		<!-- Page Content -->
		<div id="page-wrapper">

			<div class="container-fluid">
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">Plantilla...</h1>
					</div>
					<!-- /.col-lg-12 -->
				</div>
				<!-- /.row -->
			</div>
			<!-- /.container-fluid -->
            
           
          <!--  
           Estructura de contenido.
           divs-bootstrap, revisar layouts.
          -->
            <p>Contenido...</p>
            
            <modal-footer>
      <div class="row">
        <div class="col-md-2 float-right">
          <button type="button" class="btn small-button btn-success">
            <span class="fa fa-save fa-lg"></span>
            Grabar
          </button>
        </div>
        <div class="col-md-2">
          <button type="button" class="btn small-button btn-danger" (click)="cancelChgPwd()">
            <span class="fa fa-remove fa-lg"></span>
            Cancelar
          </button>
        </div>
      </div>
    </modal-footer>
           
            
        </div>
        <!-- /#page-wrapper -->
    </div>
    
  <jsp:include page="template/includes.jsp" />
  
  <!-- Scripts especificos ....  -->

</body>
</html>
