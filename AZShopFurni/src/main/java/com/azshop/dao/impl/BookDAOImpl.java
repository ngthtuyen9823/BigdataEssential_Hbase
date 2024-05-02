package com.azshop.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.filter.*;

import com.azshop.config.InitData;
import com.azshop.dao.IBookDAO;
import com.azshop.models.BookModel;

public class BookDAOImpl implements IBookDAO {
	private static final byte[] INFO_CF = Bytes.toBytes("info");
	private static final byte[] DETAIL_CF = Bytes.toBytes("detail");

	@Override
	public List<BookModel> findAll() throws IOException {
		Configuration conf = new Configuration();
		Connection connection = ConnectionFactory.createConnection(conf);
		Table table = connection.getTable(TableName.valueOf("books"));
		List<BookModel> listBook = new ArrayList<>();
		try {
			Scan scan = new Scan();
			scan.addFamily(INFO_CF);
			scan.addFamily(DETAIL_CF);
			ResultScanner scanner = table.getScanner(scan);
			for (Result result : scanner) {
				// Process each row and create a BookModel
				BookModel bookModel = new BookModel();
				bookModel.setIsbn13(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn13"))));
				bookModel.setIsbn10(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn10"))));
				bookModel.setTitle(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("title"))));
				bookModel.setAuthors(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
				bookModel.setCategories(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("categories"))));
				bookModel.setThumbnail(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("thumbnail"))));
				bookModel.setDescription(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("description"))));

				byte[] publishedYearBytes = result.getValue(DETAIL_CF, Bytes.toBytes("published_year"));
				if (publishedYearBytes != null && publishedYearBytes.length >= Bytes.SIZEOF_INT) {
					bookModel.setPublished_year(Bytes.toInt(publishedYearBytes));
				} else {
					// Handle null value or insufficient byte array length
					bookModel.setPublished_year(0); // Set a default value or log a message
				}

				byte[] averageRatingBytes = result.getValue(DETAIL_CF, Bytes.toBytes("average_rating"));
				if (averageRatingBytes != null && averageRatingBytes.length >= Bytes.SIZEOF_FLOAT) {
					bookModel.setAverage_rating(Bytes.toFloat(averageRatingBytes));
				} else {
					// Handle null value or insufficient byte array length
					bookModel.setAverage_rating(0.0f); // Set a default value or log a message
				}
//
//				byte[] numPagesBytes = result.getValue(DETAIL_CF, Bytes.toBytes("numbers"));
//				if (numPagesBytes != null && numPagesBytes.length >= Bytes.SIZEOF_INT) {
//					bookModel.setNumbers(Bytes.toInt(numPagesBytes));;
//				} else {
//					// Handle null value or insufficient byte array length
//					bookModel.setNumbers(0); // Set a default value or log a message
//				}

				byte[] ratingsCountBytes = result.getValue(DETAIL_CF, Bytes.toBytes("ratings_count"));
				if (ratingsCountBytes != null && ratingsCountBytes.length >= Bytes.SIZEOF_INT) {
					bookModel.setRatings_count(Bytes.toInt(ratingsCountBytes));
				} else {
					// Handle null value or insufficient byte array length
					bookModel.setRatings_count(0); // Set a default value or log a message
				}

				// Print or use the bookModel object
				listBook.add(bookModel);
			}
		} finally {
			table.close();
			connection.close();
		}
		return listBook;
	}

	@Override
	public BookModel findOne(String id) throws IOException {
		Configuration conf = new Configuration();
		Connection connection = ConnectionFactory.createConnection(conf);
		Table table = connection.getTable(TableName.valueOf("books"));

		Get get = new Get(Bytes.toBytes(id)); // get theo rowkey;
		BookModel bookmodel = null;
		try {
			Result result = table.get(get);
			if (!result.isEmpty()) {
				bookmodel = new BookModel();
				bookmodel.setIsbn13(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn13"))));
				bookmodel.setIsbn10(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn10"))));
				bookmodel.setTitle(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("title"))));
				bookmodel.setAuthors(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
				bookmodel.setCategories(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("categories"))));
				bookmodel.setThumbnail(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("thumbnail"))));
				bookmodel.setDescription(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("description")))); // Family
																													// -
																													// qualifier
				byte[] publishedYearBytes = result.getValue(DETAIL_CF, Bytes.toBytes("published_year"));
				if (publishedYearBytes != null && publishedYearBytes.length >= Bytes.SIZEOF_INT) {
					bookmodel.setPublished_year(Bytes.toInt(publishedYearBytes));
				} else {
					// Handle null value or insufficient byte array length
					bookmodel.setPublished_year(0); // Set a default value or log a message
				}

				byte[] averageRatingBytes = result.getValue(DETAIL_CF, Bytes.toBytes("average_rating"));
				if (averageRatingBytes != null && averageRatingBytes.length >= Bytes.SIZEOF_FLOAT) {
					bookmodel.setAverage_rating(Bytes.toFloat(averageRatingBytes));
				} else {
					// Handle null value or insufficient byte array length
					bookmodel.setAverage_rating(0.0f); // Set a default value or log a message
				}

//				byte[] numPagesBytes = result.getValue(DETAIL_CF, Bytes.toBytes("numbers"));
//				if (numPagesBytes != null && numPagesBytes.length >= Bytes.SIZEOF_INT) {
//					bookmodel.setNumbers(Bytes.toInt(numPagesBytes));;
//				} else {
//					// Handle null value or insufficient byte array length
//					bookmodel.setNumbers(0); // Set a default value or log a message
//				}

				byte[] ratingsCountBytes = result.getValue(DETAIL_CF, Bytes.toBytes("ratings_count"));
				if (ratingsCountBytes != null && ratingsCountBytes.length >= Bytes.SIZEOF_INT) {
					bookmodel.setRatings_count(Bytes.toInt(ratingsCountBytes));
				} else {
					// Handle null value or insufficient byte array length
					bookmodel.setRatings_count(0); // Set a default value or log a message
				}
			} 
			else {
				System.out.println("Empty");
			}
		}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		return bookmodel;
	}
	
	@Override
	public List<BookModel> findByName(String key) throws IOException {
		  Configuration conf = new Configuration();
		  Connection connection = ConnectionFactory.createConnection(conf);
		  Table table = connection.getTable(TableName.valueOf("books"));
		  List<BookModel> listBook = new ArrayList<>();
		  try {
		    // Create a Scan object with a RowFilter to find books by name
			  Scan scan = new Scan();
		        scan.addFamily(INFO_CF);
		        scan.addFamily(DETAIL_CF);
		        Filter filter = new SingleColumnValueFilter(INFO_CF, Bytes.toBytes("title"), CompareOperator.EQUAL, new SubstringComparator(key));
		        scan.setFilter(filter);
		        ResultScanner scanner = table.getScanner(scan);
		    for (Result result : scanner) {
		      // Process each row and create a BookModel
		      BookModel bookModel = new BookModel();
		      bookModel.setIsbn13(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn13"))));
		      bookModel.setIsbn10(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn10"))));
		      bookModel.setTitle(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("title"))));
		      bookModel.setAuthors(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
		      bookModel.setCategories(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("categories"))));
		      bookModel.setThumbnail(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("thumbnail"))));
		      bookModel.setDescription(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("description"))));
		      byte[] publishedYearBytes = result.getValue(DETAIL_CF, Bytes.toBytes("published_year"));
		      if (publishedYearBytes != null && publishedYearBytes.length >= Bytes.SIZEOF_INT) {
		        bookModel.setPublished_year(Bytes.toInt(publishedYearBytes));
		      } else {
		        // Handle null value or insufficient byte array length
		        bookModel.setPublished_year(0); // Set a default value or log a message
		      }
		      byte[] averageRatingBytes = result.getValue(DETAIL_CF, Bytes.toBytes("average_rating"));
		      if (averageRatingBytes != null && averageRatingBytes.length >= Bytes.SIZEOF_FLOAT) {
		        bookModel.setAverage_rating(Bytes.toFloat(averageRatingBytes));
		      } else {
		        // Handle null value or insufficient byte array length
		        bookModel.setAverage_rating(0.0f); // Set a default value or log a message
		      }
		      // ... (handle other fields)
		      byte[] ratingsCountBytes = result.getValue(DETAIL_CF, Bytes.toBytes("ratings_count"));
		      if (ratingsCountBytes != null && ratingsCountBytes.length >= Bytes.SIZEOF_INT) {
		        bookModel.setRatings_count(Bytes.toInt(ratingsCountBytes));
		      } else {
		        // Handle null value or insufficient byte array length
		        bookModel.setRatings_count(0); // Set a default value or log a message
		      }
		      listBook.add(bookModel);
		    }
		  } finally {
		    table.close();
		    connection.close();
		  }
		  return listBook;
		}


	@Override
	public List<BookModel> findByCategory(String cate) throws IOException  {
		Configuration conf = new Configuration();
	    Connection connection = ConnectionFactory.createConnection(conf);
	    Table table = connection.getTable(TableName.valueOf("books"));
	    List<BookModel> bookList = new ArrayList<>();

	    try {
	        Scan scan = new Scan();
	        SingleColumnValueFilter filter = new SingleColumnValueFilter(
	                INFO_CF, 
	                Bytes.toBytes("categories"), 
	                CompareOperator.EQUAL, 
	                new BinaryPrefixComparator(Bytes.toBytes(String.valueOf(cate))));
	        scan.setFilter(filter);

	        ResultScanner scanner = table.getScanner(scan);
	        for (Result result : scanner) {
	            BookModel bookModel = new BookModel();
	            bookModel.setIsbn13(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn13"))));
	            bookModel.setIsbn10(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn10"))));
	            bookModel.setTitle(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("title"))));
	            bookModel.setAuthors(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
	            bookModel.setCategories(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("categories"))));
	            bookModel.setThumbnail(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("thumbnail"))));
	            bookModel.setDescription(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("description"))));

	            byte[] publishedYearBytes = result.getValue(DETAIL_CF, Bytes.toBytes("published_year"));
	            if (publishedYearBytes != null && publishedYearBytes.length >= Bytes.SIZEOF_INT) {
	                bookModel.setPublished_year(Bytes.toInt(publishedYearBytes));
	            } else {
	                bookModel.setPublished_year(0);
	            }

	            byte[] averageRatingBytes = result.getValue(DETAIL_CF, Bytes.toBytes("average_rating"));
	            if (averageRatingBytes != null && averageRatingBytes.length >= Bytes.SIZEOF_FLOAT) {
	                bookModel.setAverage_rating(Bytes.toFloat(averageRatingBytes));
	            } else {
	                bookModel.setAverage_rating(0.0f); 
	            }

//	          byte[] numPagesBytes = result.getValue(DETAIL_CF, Bytes.toBytes("numbers"));
//	          if (numPagesBytes != null && numPagesBytes.length >= Bytes.SIZEOF_INT) {
//	              bookModel.setNumbers(Bytes.toInt(numPagesBytes));;
//	          } else {
//	              // Handle null value or insufficient byte array length
//	              bookModel.setNumbers(0); // Set a default value or log a message
//	          }

	            byte[] ratingsCountBytes = result.getValue(DETAIL_CF, Bytes.toBytes("ratings_count"));
	            if (ratingsCountBytes != null && ratingsCountBytes.length >= Bytes.SIZEOF_INT) {
	                bookModel.setRatings_count(Bytes.toInt(ratingsCountBytes));
	            } else {
	                bookModel.setRatings_count(0); 

	            bookList.add(bookModel);
	        }
	        }
	        } finally {
	        table.close();
	        connection.close();
	    }

	    return bookList;
	}


	@Override
	public List<BookModel> findByAuthor(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BookModel> findWithCount(int count) {
		List<BookModel> books = new ArrayList<>();
		Configuration conf = new Configuration();

		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("books"))) {

			Scan scan = new Scan();
			scan.addFamily(INFO_CF);
			scan.addFamily(DETAIL_CF);
			scan.setLimit(count);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	private BookModel constructBookFromResult(Result result) {
		BookModel book = new BookModel();

		book.setIsbn13(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn13"))));
		book.setIsbn10(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn10"))));
		book.setTitle(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("title"))));
		book.setAuthors(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
		book.setCategories(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("categories"))));
		book.setThumbnail(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("thumbnail"))));
		book.setDescription(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("description"))));

		byte[] publishedYearBytes = result.getValue(DETAIL_CF, Bytes.toBytes("published_year"));
		if (publishedYearBytes != null && publishedYearBytes.length >= Bytes.SIZEOF_INT) {
			book.setPublished_year(Bytes.toInt(publishedYearBytes));
		} else {
			// Handle null value or insufficient byte array length
			book.setPublished_year(0); // Set a default value or log a message
		}

		byte[] averageRatingBytes = result.getValue(DETAIL_CF, Bytes.toBytes("average_rating"));
		if (averageRatingBytes != null && averageRatingBytes.length >= Bytes.SIZEOF_FLOAT) {
			book.setAverage_rating(Bytes.toFloat(averageRatingBytes));
		} else {
			// Handle null value or insufficient byte array length
			book.setAverage_rating(0.0f); // Set a default value or log a message
		}

//		byte[] numPagesBytes = result.getValue(DETAIL_CF, Bytes.toBytes("numbers"));
//		if (numPagesBytes != null && numPagesBytes.length >= Bytes.SIZEOF_INT) {
//			book.setNumbers(Bytes.toInt(numPagesBytes));
//		} else {
//			// Handle null value or insufficient byte array length
//			book.setNumbers(0); // Set a default value or log a message
//		}

		byte[] ratingsCountBytes = result.getValue(DETAIL_CF, Bytes.toBytes("ratings_count"));
		if (ratingsCountBytes != null && ratingsCountBytes.length >= Bytes.SIZEOF_INT) {
			book.setRatings_count(Bytes.toInt(ratingsCountBytes));
		} else {
			// Handle null value or insufficient byte array length
			book.setRatings_count(0); // Set a default value or log a message
		}

		return book;
	}

	@Override
	public void insert(BookModel model) {
        try {
            if (model != null) {

            	Configuration conf = new Configuration();
            	Connection connection = ConnectionFactory.createConnection(conf);
            	Table table = connection.getTable(TableName.valueOf("books"));
            	
            	
            	Put put = new Put(Bytes.toBytes(model.getIsbn10()));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("isbn13"), Bytes.toBytes(model.getIsbn13()));
            	put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("isbn10"), Bytes.toBytes(model.getIsbn10()));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(model.getTitle()));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("authors"), Bytes.toBytes(model.getAuthors()));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("categories"), Bytes.toBytes(model.getCategories()));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("thumbnail"), Bytes.toBytes(model.getThumbnail()));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("description"), Bytes.toBytes(model.getDescription()));
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("published_year"),
                        Bytes.toBytes(model.getPublished_year()));
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("average_rating"),
                        Bytes.toBytes(model.getAverage_rating()));
//                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("numbers"), Bytes.toBytes(model.getNumbers()));
                put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("ratings_count"), Bytes.toBytes(model.getRatings_count()));

            	table.put(put);
            }     	
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void delete(String id) {
		try {
			Configuration conf = new Configuration();
        	Connection connection = ConnectionFactory.createConnection(conf);
        	Table table = connection.getTable(TableName.valueOf("books"));
            List list = new ArrayList();
            Delete delete = new Delete(id.getBytes());
            list.add(delete);
            table.delete(list);
            System.out.println("delete row successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

	@Override
	public void update(BookModel model) {
		try {
			if (model != null) {

				Configuration conf = new Configuration();
				Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("books"));
				Put put = new Put(Bytes.toBytes(model.getIsbn10()));
				
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("isbn10"), Bytes.toBytes(model.getIsbn10()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(model.getTitle()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("authors"), Bytes.toBytes(model.getAuthors()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("categories"), Bytes.toBytes(model.getCategories()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("thumbnail"), Bytes.toBytes(model.getThumbnail()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("description"),
						Bytes.toBytes(model.getDescription()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("published_year"),
						Bytes.toBytes(model.getPublished_year()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("average_rating"),
						Bytes.toBytes(model.getAverage_rating()));
//				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("numbers"), Bytes.toBytes(model.getNumbers()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("ratings_count"),
						Bytes.toBytes(model.getRatings_count()));

				table.put(put);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<BookModel> filterByRating(int rate) {
		// TODO Auto-generated method stub
		return null;
	}
	public static void main(String[] args) {
		
    }
	
}
