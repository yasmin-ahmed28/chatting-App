<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html"/>
	<xsl:template match="Message">
		<html>
			<head>
				<title>				
	 				Chat			
	 				</title>
				<style type="text/css">
	 				 @import url(https://fonts.googleapis.com/css?family=Open+Sans:300,400);	 				
	                                  .container {    width: 300px;  padding: 10px;}
					.triangle-isosceles {
													  position: relative;
													  padding: 15px;
													  margin: 1em 0 3em;
													  color: #000;
													  background: #f3961c;
													  border-radius: 10px;
													  background: linear-gradient(top, #f9d835, #f3961c);
					}
					.triangle-isosceles:after {
						  content: "";
						  display: block; 
						  position: absolute;
						  bottom: -15px;
						  left: 50px;
						  width: 0;
						  border-width: 15px 15px 0;
						  border-style: solid;
						  border-color: #f3961c transparent;
					}
                    .container1 {  margin-left:70%;  width: 300px;  padding: 10px;}
					.triangle-isosceles {
						  position:relative;
						  padding:15px;
						  margin:1em 0 3em;
						  color:#000;
						  background:#f3961c; 
						  background:-webkit-gradient(linear, 0 0, 0 100%, from(#f9d835), to(#f3961c));
						  background:-moz-linear-gradient(#f9d835, #f3961c);
						  background:-o-linear-gradient(#f9d835, #f3961c);
						  background:linear-gradient(#f9d835, #f3961c);
						  -webkit-border-radius:10px;
						  -moz-border-radius:10px;
						  border-radius:10px;
					}
					.triangle-isosceles.top {
					  background:-webkit-gradient(linear, 0 0, 0 100%, from(#f3961c), to(#f9d835));
					  background:-moz-linear-gradient(#f3961c, #f9d835);
					  background:-o-linear-gradient(#f3961c, #f9d835);
					  background:linear-gradient(#f3961c, #f9d835);
					}
	 			</style>
			</head>
			<body>
				<xsl:choose>
					<xsl:when test="type='sender'">
						<div class="container" align="left">
							<h5>
								<xsl:value-of select="from"/>
							</h5>
							<div class="triangle-isosceles">
								<div class="triangle-isosceles:after ">
									<xsl:variable name="fontsize" select="@size"/>
									<xsl:variable name="color" select="@color"/>
									<xsl:variable name="fonttype" select="@font"/>
									<font size="{fontsize} " style="{fonttype}" color="{color}">
										<xsl:value-of select="Message/from"/>
										<xsl:value-of select="body"/>
										<br>
											<h5 align="right">
												<xsl:value-of select="date"/>
											</h5>
										</br>
									</font>
								</div>
							</div>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="container1" align="right">
							<h5>
								<xsl:value-of select="from"/>
							</h5>
							<div class="triangle-isosceles ">
								<div class="triangle-isosceles.top">
									<font size="{fontsize} " style="{fonttype}" color="{color}">
										<xsl:value-of select="Message/to"/>
										<xsl:value-of select="body"/>
										<br>
											<h5 align="right">
												<xsl:value-of select="date"/>
											</h5>
										</br>
									</font>
								</div>
							</div>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
