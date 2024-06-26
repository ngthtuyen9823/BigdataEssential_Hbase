<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>InsertProduct</title>
</head>
<body>
	<main>
		<div class="container">
			<div class="row justify-content-center">
				<div class="col-lg-8">
					<div class="card shadow-lg border-0 rounded-lg mt-5">
						<div class="card-header">
							<h3 class="text-center font-weight-light my-4">Thêm sách</h3>
						</div>
						<div class="card-body">
							<form action="adminInsertBook" method="post">
								<div class="row">
									<div class="col-md-6">
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="isbn13" /> <label>ISBN
												13</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="isbn10" /> <label>ISBN
												10</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="title" /> <label>Title</label>
										</div>

										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="authors" /> <label>Authors</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="categories" />
											<label>Categories</label>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="thumbnail" />
											<label>Thumbnail</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="description" />
											<label>Description</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="published_year" />
											<label>Publish Year</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="average_rating" />
											<label>Average Rating</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="num_pages" />
											<label>Num pages</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="ratings_count" />
											<label>Ratings count</label>
										</div>


										<%-- 										<div class="form-floating mb-3">
											<select name="supplier" class="form-control">
												<option value="" style="text-align: center">--
													Select --</option>
												<c:forEach var="supplier" items="${listSupplier}">
													<option value="${supplier.supplierID}"
														style="text-align: center">${supplier.supplierName}</option>
												</c:forEach>
											</select> <label>Nhà cung cấp</label>
										</div>
										<div class="form-floating mb-3">
											<input class="form-control" type="text" name="material" /> <label>Chất
												liệu</label>
										</div> --%>
									</div>
								</div>
								<div class="text-center mt-4">
									<input type="submit" class="btn btn-primary" value="Thêm" />
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</main>
</body>
</html>